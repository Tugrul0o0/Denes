const { contextBridge, ipcRenderer } = require('electron');
const THREE = require('three');

contextBridge.exposeInMainWorld('THREE', THREE);

contextBridge.exposeInMainWorld('electronAPI', {
  // Karakter İşlemleri
  showContextMenu: () => ipcRenderer.send('show-context-menu'),
  changeCharacter: (characterName) => ipcRenderer.send('change-character', characterName),
  onCharacterChanged: (callback) => ipcRenderer.on('character-changed', (event, ...args) => callback(...args)),
  onPlayAnimation: (callback) => ipcRenderer.on('play-animation', () => callback()),

  // Not İşlemleri
  listNotes: () => ipcRenderer.invoke('notes:list'),
  readNote: (title) => ipcRenderer.invoke('notes:read', title),
  saveNote: (note) => ipcRenderer.invoke('notes:save', note),
  deleteNote: (title) => ipcRenderer.invoke('notes:delete', title)
});
