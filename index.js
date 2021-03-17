const { app, Menu, Tray, BrowserWindow } = require('electron')
const path = require('path')
var tray = null

function bookmark(){
  var win = new BrowserWindow({
    width: 500,
    height: 500,
    frame: false,
    webPreferences: {
      nodeIntegration: true
    }
  })
  win.loadFile('bookmark/index.html');
  win.on('closed',()=>{
      win = null
  })
}

function createTray() {
  tray = new Tray(path.join(__dirname, "./assets/img/logo.ico"));
  var contextMenu = Menu.buildFromTemplate([
    {
      label: '计算机', submenu: [{
        label: '系统信息'
      }, {
        label: '书签管理', click: () => {
          bookmark()
        }
      }, {
        label: '工具'
      }]
    },
    { label: '设置...' },
    { label: '关于', role: 'about' },
    { type: 'separator' },
    { label: '退出', role: 'quit' }
  ]);
  tray.setToolTip('软件开发工具包');
  tray.setContextMenu(contextMenu);

  tray.on('click', (Event) => {
    createWindow()
  })
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
}

app.whenReady().then(createTray)

//所有窗口关闭后不退出程序
app.on('window-all-closed', () => {
})

app.on('activate', () => {
  if (BrowserWindow.getAllWindows().length === 0) {
    createWindow()
  }
})
