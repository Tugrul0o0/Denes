// window.THREE, preload.js tarafından güvenli bir şekilde sağlanır.
// TODO: Bu geometrik şekiller, gelecekte gerçek 3D anime karakter modelleriyle değiştirilmelidir.
// GLTFLoader kullanılarak .gltf veya .glb formatındaki modeller yüklenebilir.

const scene = new THREE.Scene();
const camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);
const canvas = document.getElementById('three-canvas');
const infoOverlay = document.getElementById('info-overlay');
const renderer = new THREE.WebGLRenderer({
  canvas: canvas,
  alpha: true
});

renderer.setSize(window.innerWidth, window.innerHeight);

window.addEventListener('resize', () => {
  renderer.setSize(window.innerWidth, window.innerHeight);
  camera.aspect = window.innerWidth / window.innerHeight;
  camera.updateProjectionMatrix();
});

let characterMesh;
let isAnimatingJump = false;
let jumpVelocity = 0;
const initialY = 0; // Karakterin başlangıç Y pozisyonu
const gravity = -0.01;

function changeCharacter(characterName) {
    if (characterMesh) {
        scene.remove(characterMesh);
    }
    let geometry, material;
    switch (characterName) {
        case 'sphere':
            geometry = new THREE.SphereGeometry(0.7, 32, 32);
            material = new THREE.MeshStandardMaterial({ color: 0x0000ff });
            break;
        case 'cone':
            geometry = new THREE.ConeGeometry(0.7, 1.5, 32);
            material = new THREE.MeshStandardMaterial({ color: 0xff0000 });
            break;
        case 'cube':
        default:
            geometry = new THREE.BoxGeometry(1, 1, 1);
            material = new THREE.MeshStandardMaterial({ color: 0x00ff00 });
            break;
    }
    characterMesh = new THREE.Mesh(geometry, material);
    characterMesh.position.y = initialY;
    scene.add(characterMesh);
    if (infoOverlay) {
        infoOverlay.classList.add('hidden');
    }
}

changeCharacter('cube');

const ambientLight = new THREE.AmbientLight(0xffffff, 0.5);
scene.add(ambientLight);
const directionalLight = new THREE.DirectionalLight(0xffffff, 1);
directionalLight.position.set(5, 5, 5);
scene.add(directionalLight);
camera.position.z = 5;

function animate() {
  requestAnimationFrame(animate);

  if (characterMesh) {
    // Normal animasyon (dönme)
    characterMesh.rotation.x += 0.01;
    characterMesh.rotation.y += 0.01;

    // Zıplama animasyonu
    if (isAnimatingJump) {
        characterMesh.position.y += jumpVelocity;
        jumpVelocity += gravity;
        if (characterMesh.position.y <= initialY) {
            characterMesh.position.y = initialY;
            isAnimatingJump = false;
        }
    }
  }
  renderer.render(scene, camera);
}

animate();

// --- IPC Dinleyicileri ---

window.electronAPI.onCharacterChanged((characterName) => {
    console.log('Character changed to:', characterName);
    changeCharacter(characterName);
});

window.electronAPI.onPlayAnimation(() => {
    // Eğer zaten zıplamıyorsa yeni bir zıplama başlat
    if (!isAnimatingJump) {
        isAnimatingJump = true;
        jumpVelocity = 0.2; // Zıplama başlangıç hızı
    }
});

canvas.addEventListener('contextmenu', (event) => {
  event.preventDefault();
  window.electronAPI.showContextMenu();
});

console.log('Three.js scene initialized.');
