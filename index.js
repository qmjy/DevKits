const { app, Menu, Tray, BrowserWindow } = require('electron')
const path = require('path')
var appIcon = null

function createTray() {
  appIcon = new Tray(path.join(__dirname, "./assets/logo.ico"));
  var contextMenu = Menu.buildFromTemplate([
    {
      label: '计算机', submenu: [{
        label: '系统信息'
      }, {
        label: '工具'
      }]
    },
    { label: '设置...' },
    { label: '关于', role: 'about' },
    { type: 'separator' },
    { label: '退出', role: 'quit' }
  ]);
  appIcon.setToolTip('软件开发工具包');
  appIcon.setContextMenu(contextMenu);
}


function createWindow() {
  const win = new BrowserWindow({
    width: 800,
    height: 600,
    webPreferences: {
      nodeIntegration: true
    }
  })

  win.loadFile('index.html')

  createTray()
}

app.whenReady().then(createWindow)

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit()
  }
})

app.on('activate', () => {
  if (BrowserWindow.getAllWindows().length === 0) {
    createWindow()
  }
})
