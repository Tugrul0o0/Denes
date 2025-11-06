const { app, BrowserWindow, ipcMain, globalShortcut, Menu, Tray, nativeImage } = require('electron');
const path = require('path');
const fs = require('fs');

let mainWindow;
let panelWindow;
let tray;

// Notların saklanacağı klasörün yolunu tanımla
const notesDir = path.join(app.getPath('userData'), 'notes');
// Uygulama başladığında notlar klasörünün var olduğundan emin ol
if (!fs.existsSync(notesDir)) {
  fs.mkdirSync(notesDir);
}

function createWindow() {
  mainWindow = new BrowserWindow({
    width: 800,
    height: 600,
    transparent: true,
    frame: false,
    alwaysOnTop: true,
    webPreferences: {
      preload: path.join(__dirname, 'preload.js'),
      contextIsolation: true,
      nodeIntegration: false,
    },
  });
  mainWindow.loadFile('src/index.html');
  mainWindow.on('closed', () => {
    mainWindow = null;
  });
}

function createPanelWindow() {
  if (panelWindow) {
    panelWindow.focus();
    return;
  }
  panelWindow = new BrowserWindow({
    width: 400,
    height: 600,
    webPreferences: {
      preload: path.join(__dirname, 'preload.js'),
      contextIsolation: true,
      nodeIntegration: false,
    },
  });
  panelWindow.loadFile('src/panel.html');
  panelWindow.on('closed', () => {
    panelWindow = null;
  });
}

app.whenReady().then(() => {
  createWindow();

  // Sistem tepsisi için boş (varsayılan) bir ikon oluştur
  const icon = nativeImage.createEmpty();
  tray = new Tray(icon);
  const trayContextMenu = Menu.buildFromTemplate([
    { label: 'Kontrol Paneli', click: createPanelWindow },
    { label: 'Çıkış', click: () => app.quit() }
  ]);
  tray.setToolTip('Desktop Pet');
  tray.setContextMenu(trayContextMenu);

  // --- IPC Dinleyicileri ---

  // Karakter Değiştirme
  ipcMain.on('change-character', (event, characterName) => {
    if (mainWindow) {
      mainWindow.webContents.send('character-changed', characterName);
    }
  });

  // Sağ Tıklama Menüsü
  ipcMain.on('show-context-menu', () => {
    const contextMenu = Menu.buildFromTemplate([
      {
        label: 'Animasyon Oynat',
        click: () => {
          if(mainWindow) mainWindow.webContents.send('play-animation');
        }
      },
      { type: 'separator' },
      { label: 'Tıklamayı Kapat (Geri almak için Ctrl+R+T)', click: () => mainWindow.setIgnoreMouseEvents(true, { forward: true }) },
      { type: 'separator' },
      { label: 'Kontrol Paneli', click: createPanelWindow }
    ]);
    contextMenu.popup({ window: mainWindow });
  });

  // Not Alma İşlemleri (Asenkron)
  ipcMain.handle('notes:list', async () => {
    const files = await fs.promises.readdir(notesDir);
    return files.filter(file => file.endsWith('.txt')).map(file => file.replace('.txt', ''));
  });

  ipcMain.handle('notes:read', async (event, title) => {
    const filePath = path.join(notesDir, `${title}.txt`);
    if (fs.existsSync(filePath)) {
      return await fs.promises.readFile(filePath, 'utf-8');
    }
    return null;
  });

  ipcMain.handle('notes:save', async (event, { title, content }) => {
    const filePath = path.join(notesDir, `${title}.txt`);
    await fs.promises.writeFile(filePath, content, 'utf-8');
    return { success: true };
  });

  ipcMain.handle('notes:delete', async (event, title) => {
    const filePath = path.join(notesDir, `${title}.txt`);
    if (fs.existsSync(filePath)) {
      await fs.promises.unlink(filePath);
      return { success: true };
    }
    return { success: false };
  });

  // Global Kısayol
  globalShortcut.register('CommandOrControl+R+T', () => {
    if(mainWindow) mainWindow.setIgnoreMouseEvents(false);
  });

  app.on('activate', function () {
    if (BrowserWindow.getAllWindows().length === 0) createWindow();
  });
});

app.on('will-quit', () => {
  globalShortcut.unregisterAll();
});

app.on('window-all-closed', function () {
  if (process.platform !== 'darwin') app.quit();
});
