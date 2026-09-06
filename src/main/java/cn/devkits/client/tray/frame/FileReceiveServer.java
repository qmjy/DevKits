package cn.devkits.client.tray.frame;

import cn.devkits.client.util.DKSysUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class FileReceiveServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileReceiveServer.class);
    private static final int DEFAULT_PORT = 9876;
    private static final int MAX_PORT_ATTEMPTS = 50;
    private static final int BUFFER_SIZE = 64 * 1024;
    private static final String UPLOAD_PAGE_PATH = "web/file_receive.html";

    private HttpServer httpServer;
    private ExecutorService executor;
    private File saveDir;
    private String uploadPage;
    private final AtomicInteger taskSeq = new AtomicInteger();
    private final Map<Integer, TransferTask> tasks = new ConcurrentHashMap<>();

    public synchronized void start() throws IOException {
        if (httpServer != null) return;
        saveDir = resolveSaveDir();
        if (!saveDir.exists() && !saveDir.mkdirs()) {
            throw new IOException("Can not create save directory: " + saveDir.getAbsolutePath());
        }
        IOException lastException = null;
        for (int i = 0; i < MAX_PORT_ATTEMPTS; i++) {
            try {
                httpServer = HttpServer.create(new InetSocketAddress(DEFAULT_PORT + i), 0);
                break;
            } catch (IOException e) {
                lastException = e;
                LOGGER.warn("Port {} is occupied, try next one", DEFAULT_PORT + i);
            }
        }
        if (httpServer == null) {
            throw lastException != null ? lastException : new IOException("No available port for file receive server");
        }
        executor = Executors.newCachedThreadPool(runnable -> {
            Thread thread = new Thread(runnable, "file-receive-worker");
            thread.setDaemon(true);
            return thread;
        });
        httpServer.setExecutor(executor);
        httpServer.createContext("/", new RootPageHandler());
        httpServer.createContext("/upload", new UploadHandler());
        uploadPage = loadUploadPage();
        httpServer.start();
        LOGGER.info("File receive server started on port {}", getPort());
    }

    public synchronized void stop() {
        if (httpServer != null) { httpServer.stop(1); httpServer = null; }
        if (executor != null) { executor.shutdownNow(); executor = null; }
    }

    public boolean isRunning() { return httpServer != null; }
    public int getPort() { return httpServer != null ? httpServer.getAddress().getPort() : DEFAULT_PORT; }
    public File getSaveDir() { return saveDir; }

    public List<TransferTask> getTasks() {
        List<TransferTask> snapshot = new ArrayList<>(tasks.values());
        snapshot.sort(Comparator.comparingInt(t -> t.id));
        return snapshot;
    }

    private String loadUploadPage() throws IOException {
        try (InputStream in = FileReceiveServer.class.getClassLoader().getResourceAsStream(UPLOAD_PAGE_PATH)) {
            if (in == null) throw new IOException("Upload page not found: " + UPLOAD_PAGE_PATH);
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public static File resolveSaveDir() {
        if (DKSysUtil.isWindows()) return FileSystemView.getFileSystemView().getDefaultDirectory();
        return new File(System.getProperty("user.home"), "Documents");
    }

    private class RootPageHandler implements com.sun.net.httpserver.HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String path = exchange.getRequestURI().getPath();
                if ("/".equals(path) || "/index.html".equals(path)) {
                    byte[] body = uploadPage.getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
                    exchange.sendResponseHeaders(200, body.length);
                    try (OutputStream os = exchange.getResponseBody()) { os.write(body); }
                } else {
                    exchange.sendResponseHeaders(404, -1);
                }
            } finally { exchange.close(); }
        }
    }

    private class UploadHandler implements com.sun.net.httpserver.HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            TransferTask task = null;
            Path target = null;
            PausedException pausedEx = null;
            try {
                if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(405, -1);
                    return;
                }
                long[] rangeInfo = parseContentRange(exchange);
                long rangeStart = rangeInfo[0];
                long totalFromRange = rangeInfo[1];
                String fileName = parseFileName(exchange);
                long contentLength = parseContentLength(exchange);

                task = findOrCreateTask(fileName);
                if (task.totalSize <= 0) {
                    if (totalFromRange > 0) {
                        task.totalSize = totalFromRange;
                    } else if (rangeStart == 0 && contentLength > 0) {
                        task.totalSize = contentLength;
                    }
                }
                if (task.cancelled) { respondText(exchange, 409, "CANCELLED"); return; }

                target = resolveTarget(task, fileName);
                long existing = Files.exists(target) ? Files.size(target) : 0L;

                if (task.paused) {
                    task.receivedSize = existing;
                    task.status = TransferTask.Status.PAUSED;
                    respondText(exchange, 418, "PAUSED:" + existing);
                    return;
                }
                if (rangeStart > 0 && rangeStart != existing) {
                    // 客户端续传偏移与实际文件不一致，告知真实断点，客户端将从该位置重试
                    task.receivedSize = existing;
                    respondText(exchange, 418, "PAUSED:" + existing);
                    return;
                }
                task.receivedSize = rangeStart;
                task.status = TransferTask.Status.TRANSFERRING;

                java.nio.file.OpenOption[] openOpts = (rangeStart > 0)
                        ? new java.nio.file.OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND}
                        : new java.nio.file.OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING};

                try (InputStream in = exchange.getRequestBody();
                     OutputStream out = Files.newOutputStream(target, openOpts)) {
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int read;
                    while ((read = in.read(buffer)) > 0) {
                        if (task.cancelled) break;
                        if (task.paused) {
                            out.write(buffer, 0, read);
                            task.receivedSize += read;
                            throw new PausedException(task.receivedSize);
                        }
                        out.write(buffer, 0, read);
                        task.receivedSize += read;
                    }
                } catch (PausedException pe) { pausedEx = pe; }

                if (task.cancelled) {
                    task.status = TransferTask.Status.CANCELLED;
                    Files.deleteIfExists(target);
                    respondText(exchange, 409, "CANCELLED");
                } else if (pausedEx != null) {
                    task.status = TransferTask.Status.PAUSED;
                    respondText(exchange, 418, "PAUSED:" + pausedEx.breakpoint);
                } else if (task.totalSize <= 0 || task.receivedSize >= task.totalSize) {
                    task.status = TransferTask.Status.DONE;
                    task.file = target.toFile();
                    respondText(exchange, 200, "OK");
                } else {
                    // 客户端提前断开（半关闭）导致传输不完整，保留半成品文件等待自动续传
                    task.status = TransferTask.Status.PAUSED;
                    respondText(exchange, 418, "PAUSED:" + task.receivedSize);
                }
            } catch (IOException e) {
                LOGGER.warn("Receive file interrupted: {}", e.getMessage());
                // 连接中断：保留半成品文件，标记为可续传状态，供客户端自动重连续传
                if (task != null && task.getStatus() == TransferTask.Status.TRANSFERRING) {
                    task.status = TransferTask.Status.PAUSED;
                }
                if (task != null && !task.cancelled && !task.paused) {
                    try { respondText(exchange, 418, "PAUSED:" + task.receivedSize); } catch (RuntimeException | IOException ignored) {}
                }
            } finally { exchange.close(); }
        }

        private static class PausedException extends Exception {
            final long breakpoint;
            PausedException(long breakpoint) { this.breakpoint = breakpoint; }
        }

        private TransferTask findOrCreateTask(String fileName) {
            for (TransferTask t : tasks.values()) {
                // 已完成/已取消的任务不可复用；中断（传输中/暂停/失败）的任务同名续传时复用，避免重复行与文件错乱
                if ((t.originalName.equals(fileName) || t.fileName.equals(fileName))
                        && t.status != TransferTask.Status.DONE
                        && t.status != TransferTask.Status.CANCELLED) {
                    return t;
                }
            }
            TransferTask task = new TransferTask(taskSeq.incrementAndGet(), fileName);
            tasks.put(task.id, task);
            return task;
        }

        private long[] parseContentRange(HttpExchange exchange) {
            String cr = exchange.getRequestHeaders().getFirst("Content-Range");
            long[] result = new long[2];
            if (cr == null) return result;
            try {
                String body = cr.trim();
                if (body.startsWith("bytes ")) body = body.substring(6);
                int slash = body.indexOf('/');
                if (slash > 0) {
                    String rangePart = body.substring(0, slash);
                    String totalPart = body.substring(slash + 1);
                    int dash = rangePart.indexOf('-');
                    if (dash > 0) result[0] = Long.parseLong(rangePart.substring(0, dash));
                    result[1] = Long.parseLong(totalPart);
                }
            } catch (NumberFormatException ignored) {}
            return result;
        }

        private Path resolveTarget(TransferTask task, String fileName) {
            if (task.file != null) return task.file.toPath();
            File t = uniqueTarget(saveDir, fileName);
            task.fileName = t.getName();
            task.file = t;
            return t.toPath();
        }

        private void respondText(HttpExchange exchange, int code, String text) throws IOException {
            byte[] body = text.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
            exchange.sendResponseHeaders(code, body.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(body); }
        }
    }

    private long parseContentLength(HttpExchange exchange) {
        String length = exchange.getRequestHeaders().getFirst("Content-Length");
        if (length != null) {
            try { return Long.parseLong(length.trim()); } catch (NumberFormatException ignored) {}
        }
        return 0;
    }

    private String parseFileName(HttpExchange exchange) {
        String query = exchange.getRequestURI().getRawQuery();
        if (query != null) {
            for (String pair : query.split("&")) {
                int eq = pair.indexOf('=');
                if (eq > 0 && "name".equals(pair.substring(0, eq))) {
                    return sanitizeFileName(URLDecoder.decode(pair.substring(eq + 1), StandardCharsets.UTF_8));
                }
            }
        }
        return "unnamed";
    }

    private static String sanitizeFileName(String name) {
        if (name == null || name.isBlank()) return "unnamed";
        String cleaned = name.replaceAll("[\\\\/:*?\\\"<>|\\p{Cntrl}]", "_").trim();
        if (cleaned.length() > 150) {
            String ext = "";
            int dot = cleaned.lastIndexOf('.');
            if (dot > 0) ext = cleaned.substring(dot);
            cleaned = cleaned.substring(0, 150 - ext.length()) + ext;
        }
        return cleaned.isBlank() ? "unnamed" : cleaned;
    }

    private static File uniqueTarget(File dir, String name) {
        File target = new File(dir, name);
        if (!target.exists()) return target;
        String base = name;
        String ext = "";
        int dot = name.lastIndexOf('.');
        if (dot > 0) { base = name.substring(0, dot); ext = name.substring(dot); }
        int seq = 1;
        while (target.exists()) target = new File(dir, base + "(" + seq++ + ")" + ext);
        return target;
    }

    public static class TransferTask {
        private final int id;
        private final String originalName;
        private volatile String fileName;
        private volatile long totalSize;
        private volatile long receivedSize;
        private volatile Status status = Status.TRANSFERRING;
        private volatile File file;
        private volatile boolean paused;
        private volatile boolean cancelled;

        private TransferTask(int id, String fileName) {
            this.id = id;
            this.originalName = fileName;
            this.fileName = fileName;
        }

        public int getId() { return id; }
        public String getFileName() { return fileName; }
        public long getTotalSize() { return totalSize; }
        public long getReceivedSize() { return receivedSize; }
        public Status getStatus() { return status; }
        public File getFile() { return file; }
        public void setStatus(Status status) { this.status = status; }
        public boolean isCancelled() { return cancelled; }

        public void cancel() { this.cancelled = true; this.status = Status.CANCELLED; }

        public float getProgress() {
            if (status == Status.DONE) return 1f;
            if (totalSize <= 0) return 0f;
            // 使用 double 计算避免大文件（>16MB）浮点精度损失
            return (float) Math.min(1.0, receivedSize / (double) totalSize);
        }

        public enum Status { TRANSFERRING, PAUSED, DONE, FAILED, CANCELLED }
    }
}
