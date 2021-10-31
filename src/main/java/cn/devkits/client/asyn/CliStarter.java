package cn.devkits.client.asyn;

import cn.devkits.client.camera.CameraFrame;

/**
 * <p>
 * 命令行启动器
 * </p>
 *
 * @author Shaofeng Liu
 * @since 2021/10/31
 */
public class CliStarter extends BaseStarter implements Runnable {
    private final String[] args;

    public CliStarter(String[] args) {
        super();
        this.args = args;
    }

    @Override
    public void run() {
        initLookAndFeel();
        new CameraFrame();
    }
}
