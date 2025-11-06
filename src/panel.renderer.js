// --- Değişkenler ---
const characterList = document.getElementById('character-list');
const notesList = document.getElementById('notes-list');
const noteTitle = document.getElementById('note-title');
const noteContent = document.getElementById('note-content');
const btnNewNote = document.getElementById('btn-new-note');
const btnSaveNote = document.getElementById('btn-save-note');
const btnDeleteNote = document.getElementById('btn-delete-note');

let selectedNoteTitle = null;

// --- Karakter Seçimi ---
characterList.addEventListener('click', (event) => {
    if (event.target.tagName === 'LI') {
        const characterName = event.target.dataset.character;
        window.electronAPI.changeCharacter(characterName);
    }
});

// --- Not Defteri ---

// Not listesini dolduran fonksiyon
async function populateNotesList() {
    const notes = await window.electronAPI.listNotes();
    notesList.innerHTML = ''; // Listeyi temizle
    for (const note of notes) {
        const li = document.createElement('li');
        li.textContent = note;
        if (note === selectedNoteTitle) {
            li.classList.add('selected');
        }
        notesList.appendChild(li);
    }
}

// Not seçildiğinde içeriği yükle
notesList.addEventListener('click', async (event) => {
    if (event.target.tagName === 'LI') {
        const title = event.target.textContent;
        const content = await window.electronAPI.readNote(title);
        noteTitle.value = title;
        noteContent.value = content;
        selectedNoteTitle = title;
        populateNotesList(); // Seçimi görsel olarak güncelle
    }
});

// Yeni Not butonu
btnNewNote.addEventListener('click', () => {
    selectedNoteTitle = null;
    noteTitle.value = '';
    noteContent.value = '';
    populateNotesList();
});

// Kaydet butonu
btnSaveNote.addEventListener('click', async () => {
    const title = noteTitle.value.trim();
    const content = noteContent.value;
    if (!title) {
        alert('Lütfen bir not başlığı girin.');
        return;
    }
    await window.electronAPI.saveNote({ title, content });
    selectedNoteTitle = title;
    await populateNotesList();
    alert('Not kaydedildi!');
});

// Sil butonu
btnDeleteNote.addEventListener('click', async () => {
    if (!selectedNoteTitle) {
        alert('Lütfen silmek için bir not seçin.');
        return;
    }
    const confirmDelete = confirm(`'${selectedNoteTitle}' başlıklı notu silmek istediğinizden emin misiniz?`);
    if (confirmDelete) {
        await window.electronAPI.deleteNote(selectedNoteTitle);
        btnNewNote.click(); // Editörü temizle
        await populateNotesList();
    }
});


// Başlangıçta not listesini yükle
populateNotesList();
