// Configuración global de la aplicación
const APP_CONFIG = {
    currentLevel: 1,
    currentLetter: 'A',
    totalStars: 0,
    sessionStartTime: Date.now(),
    voiceSupported: 'webkitSpeechRecognition' in window || 'SpeechRecognition' in window
};

// Datos de letras con palabras y sonidos asociados
const LETTER_DATA = {
    // Vocales (Nivel 1)
    'A': { words: ['AVIÓN', 'ÁRBOL', 'AGUA'], sound: '✈️', level: 1 },
    'E': { words: ['ELEFANTE', 'ESTRELLA', 'ESCUELA'], sound: '🐘', level: 1 },
    'I': { words: ['IGLÚ', 'ISLA', 'IMÁN'], sound: '🏠', level: 1 },
    'O': { words: ['OSO', 'OJO', 'OVEJA'], sound: '🐻', level: 1 },
    'U': { words: ['UVA', 'UÑA', 'UNICORNIO'], sound: '🍇', level: 1 },
    
    // Consonantes comunes (Nivel 2)
    'B': { words: ['BARCO', 'BOLA', 'BEBÉ'], sound: '⛵', level: 2 },
    'C': { words: ['COCHE', 'CASA', 'CAMA'], sound: '🚗', level: 2 },
    'D': { words: ['DELFÍN', 'DADO', 'DEDO'], sound: '🐬', level: 2 },
    'F': { words: ['FLOR', 'FUEGO', 'FOCA'], sound: '🌸', level: 2 },
    'G': { words: ['GATO', 'GLOBO', 'GUITARRA'], sound: '🐱', level: 2 },
    'H': { words: ['HELADO', 'HOJA', 'HUEVO'], sound: '🍦', level: 2 },
    'J': { words: ['JIRAFA', 'JUGO', 'JARDÍN'], sound: '🦒', level: 2 },
    'K': { words: ['KIWI', 'KARATE', 'KOALA'], sound: '🥝', level: 2 },
    'L': { words: ['LEÓN', 'LUNA', 'LIBRO'], sound: '🦁', level: 2 },
    'M': { words: ['MAMÁ', 'MANO', 'MESA'], sound: '👩', level: 2 },
    'N': { words: ['NUBE', 'NARIZ', 'NIDO'], sound: '☁️', level: 2 },
    'Ñ': { words: ['NIÑO', 'ÑANDÚ', 'CAÑA'], sound: '👶', level: 2 },
    'P': { words: ['PAPÁ', 'PELOTA', 'PEZ'], sound: '👨', level: 2 },
    'Q': { words: ['QUESO', 'QUETZAL', 'QUINOA'], sound: '🧀', level: 2 },
    'R': { words: ['RATÓN', 'ROSA', 'RELOJ'], sound: '🐭', level: 2 },
    'S': { words: ['SOL', 'SAPO', 'SILLA'], sound: '☀️', level: 2 },
    'T': { words: ['TIGRE', 'TREN', 'TORTUGA'], sound: '🐅', level: 2 },
    'V': { words: ['VACA', 'VIENTO', 'VIOLÍN'], sound: '🐄', level: 2 },
    'W': { words: ['WIFI', 'WAFFLE', 'WHISKY'], sound: '📶', level: 2 },
    'X': { words: ['XILÓFONO', 'TAXI', 'BOXEO'], sound: '🎵', level: 2 },
    'Y': { words: ['YOYO', 'YATE', 'YOGUR'], sound: '🪀', level: 2 },
    'Z': { words: ['ZAPATO', 'ZORRO', 'ZONA'], sound: '👟', level: 2 }
};

// Sistema de progreso del usuario
class ProgressSystem {
    constructor() {
        this.data = this.loadProgress();
    }

    loadProgress() {
        const saved = localStorage.getItem('abc-aprende-progress');
        if (saved) {
            return JSON.parse(saved);
        }
        
        // Progreso inicial
        const initialProgress = {
            totalStars: 0,
            totalTime: 0,
            letterProgress: {},
            unlockedLevels: [1],
            achievements: []
        };
        
        // Inicializar progreso de cada letra
        Object.keys(LETTER_DATA).forEach(letter => {
            initialProgress.letterProgress[letter] = {
                stars: 0,
                attempts: 0,
                successes: 0,
                lastPracticed: null,
                mastered: false
            };
        });
        
        return initialProgress;
    }

    saveProgress() {
        localStorage.setItem('abc-aprende-progress', JSON.stringify(this.data));
    }

    addStars(letter, stars) {
        this.data.totalStars += stars;
        this.data.letterProgress[letter].stars += stars;
        this.data.letterProgress[letter].successes++;
        this.data.letterProgress[letter].lastPracticed = Date.now();
        
        // Verificar si la letra está dominada (3 estrellas)
        if (this.data.letterProgress[letter].stars >= 3) {
            this.data.letterProgress[letter].mastered = true;
        }
        
        this.checkLevelUnlock();
        this.saveProgress();
        this.updateUI();
    }

    addAttempt(letter) {
        this.data.letterProgress[letter].attempts++;
        this.saveProgress();
    }

    checkLevelUnlock() {
        // Desbloquear nivel 2 si se dominan 3 vocales
        const vowelsMastered = ['A', 'E', 'I', 'O', 'U'].filter(
            letter => this.data.letterProgress[letter].mastered
        ).length;
        
        if (vowelsMastered >= 3 && !this.data.unlockedLevels.includes(2)) {
            this.data.unlockedLevels.push(2);
            this.showAchievement('¡Nivel 2 Desbloqueado!', '🎉');
        }
        
        // Desbloquear nivel 3 si se dominan 10 consonantes
        const consonantsMastered = Object.keys(LETTER_DATA).filter(
            letter => LETTER_DATA[letter].level === 2 && this.data.letterProgress[letter].mastered
        ).length;
        
        if (consonantsMastered >= 10 && !this.data.unlockedLevels.includes(3)) {
            this.data.unlockedLevels.push(3);
            this.showAchievement('¡Nivel 3 Desbloqueado!', '🏆');
        }
    }

    showAchievement(message, icon) {
        const achievement = document.createElement('div');
        achievement.className = 'achievement-popup';
        achievement.innerHTML = `
            <div class="achievement-content">
                <span class="achievement-icon">${icon}</span>
                <span class="achievement-text">${message}</span>
            </div>
        `;
        
        document.body.appendChild(achievement);
        
        setTimeout(() => {
            achievement.remove();
        }, 3000);
    }

    updateUI() {
        // Actualizar contador de estrellas
        document.getElementById('total-stars').textContent = this.data.totalStars;
        document.getElementById('current-stars').textContent = this.data.totalStars;
        
        // Actualizar niveles desbloqueados
        document.querySelectorAll('.level-btn').forEach((btn, index) => {
            const level = index + 1;
            if (this.data.unlockedLevels.includes(level)) {
                btn.classList.remove('locked');
            } else {
                btn.classList.add('locked');
            }
        });
    }

    getAccuracyRate() {
        const letters = Object.values(this.data.letterProgress);
        const totalAttempts = letters.reduce((sum, letter) => sum + letter.attempts, 0);
        const totalSuccesses = letters.reduce((sum, letter) => sum + letter.successes, 0);
        
        return totalAttempts > 0 ? Math.round((totalSuccesses / totalAttempts) * 100) : 0;
    }

    getMasteredCount() {
        return Object.values(this.data.letterProgress).filter(letter => letter.mastered).length;
    }

    reset() {
        localStorage.removeItem('abc-aprende-progress');
        this.data = this.loadProgress();
        this.updateUI();
        location.reload();
    }
}

// Sistema de Text-to-Speech
class TTSSystem {
    constructor() {
        this.synth = window.speechSynthesis;
        this.voice = null;
        this.initVoice();
    }

    initVoice() {
        const setVoice = () => {
            const voices = this.synth.getVoices();
            // Buscar voz en español
            this.voice = voices.find(voice => 
                voice.lang.includes('es') || voice.name.includes('Spanish')
            ) || voices[0];
        };

        if (this.synth.getVoices().length > 0) {
            setVoice();
        } else {
            this.synth.addEventListener('voiceschanged', setVoice);
        }
    }

    speak(text, rate = 1) {
        if (this.synth.speaking) {
            this.synth.cancel();
        }

        const utterance = new SpeechSynthesisUtterance(text);
        utterance.voice = this.voice;
        utterance.rate = rate;
        utterance.pitch = 1.2;
        utterance.volume = 1;

        this.synth.speak(utterance);
    }

    speakLetter(letter) {
        this.speak(letter, 0.8);
    }

    speakWord(word) {
        this.speak(word, 0.9);
    }

    speakSlow(text) {
        // Separar por sílabas de manera simple
        const syllables = this.splitIntoSyllables(text);
        let delay = 0;
        
        syllables.forEach((syllable, index) => {
            setTimeout(() => {
                this.speak(syllable, 0.6);
            }, delay);
            delay += 800;
        });
    }

    splitIntoSyllables(word) {
        // Implementación simple de separación silábica
        const vowels = 'aeiouáéíóúAEIOUÁÉÍÓÚ';
        const syllables = [];
        let currentSyllable = '';
        
        for (let i = 0; i < word.length; i++) {
            currentSyllable += word[i];
            
            if (vowels.includes(word[i])) {
                // Si la siguiente letra es consonante o es el final
                if (i === word.length - 1 || !vowels.includes(word[i + 1])) {
                    syllables.push(currentSyllable);
                    currentSyllable = '';
                }
            }
        }
        
        if (currentSyllable) {
            syllables.push(currentSyllable);
        }
        
        return syllables.length > 0 ? syllables : [word];
    }
}

// Sistema de reconocimiento de voz
class VoiceRecognitionSystem {
    constructor() {
        this.recognition = null;
        this.isListening = false;
        this.initRecognition();
    }

    initRecognition() {
        if (!APP_CONFIG.voiceSupported) {
            console.log('Reconocimiento de voz no soportado');
            return;
        }

        const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
        this.recognition = new SpeechRecognition();
        
        this.recognition.lang = 'es-ES';
        this.recognition.continuous = false;
        this.recognition.interimResults = false;
        this.recognition.maxAlternatives = 3;

        this.recognition.onstart = () => {
            this.isListening = true;
            this.updateVoiceUI(true);
        };

        this.recognition.onend = () => {
            this.isListening = false;
            this.updateVoiceUI(false);
        };

        this.recognition.onerror = (event) => {
            console.error('Error de reconocimiento:', event.error);
            this.showVoiceFeedback('No pude escucharte. ¡Inténtalo de nuevo!', 'error');
        };
    }

    startListening(targetWord, callback) {
        if (!this.recognition || this.isListening) return;

        this.targetWord = targetWord.toLowerCase();
        this.callback = callback;

        this.recognition.onresult = (event) => {
            const result = event.results[0][0].transcript.toLowerCase();
            const confidence = event.results[0][0].confidence;
            
            console.log('Reconocido:', result, 'Confianza:', confidence);
            
            this.evaluateResult(result, confidence);
        };

        this.recognition.start();
    }

    evaluateResult(spokenText, confidence) {
        const similarity = this.calculateSimilarity(spokenText, this.targetWord);
        let stars = 0;
        let message = '';
        let type = '';

        if (similarity >= 0.8 || confidence >= 0.8) {
            stars = 3;
            message = '¡Perfecto! 🌟';
            type = 'success';
        } else if (similarity >= 0.6 || confidence >= 0.6) {
            stars = 2;
            message = '¡Muy bien! 👍';
            type = 'warning';
        } else if (similarity >= 0.4 || confidence >= 0.4) {
            stars = 1;
            message = '¡Buen intento! 😊';
            type = 'warning';
        } else {
            stars = 0;
            message = 'Inténtalo de nuevo 🤔';
            type = 'error';
        }

        this.showVoiceFeedback(message, type);
        
        if (this.callback) {
            this.callback(stars, spokenText);
        }
    }

    calculateSimilarity(str1, str2) {
        // Algoritmo simple de similitud de cadenas
        const longer = str1.length > str2.length ? str1 : str2;
        const shorter = str1.length > str2.length ? str2 : str1;
        
        if (longer.length === 0) return 1.0;
        
        const distance = this.levenshteinDistance(longer, shorter);
        return (longer.length - distance) / longer.length;
    }

    levenshteinDistance(str1, str2) {
        const matrix = [];
        
        for (let i = 0; i <= str2.length; i++) {
            matrix[i] = [i];
        }
        
        for (let j = 0; j <= str1.length; j++) {
            matrix[0][j] = j;
        }
        
        for (let i = 1; i <= str2.length; i++) {
            for (let j = 1; j <= str1.length; j++) {
                if (str2.charAt(i - 1) === str1.charAt(j - 1)) {
                    matrix[i][j] = matrix[i - 1][j - 1];
                } else {
                    matrix[i][j] = Math.min(
                        matrix[i - 1][j - 1] + 1,
                        matrix[i][j - 1] + 1,
                        matrix[i - 1][j] + 1
                    );
                }
            }
        }
        
        return matrix[str2.length][str1.length];
    }

    updateVoiceUI(isListening) {
        const waves = document.querySelectorAll('.wave');
        const micButton = document.getElementById('start-recording');
        
        if (isListening) {
            waves.forEach(wave => wave.style.animationPlayState = 'running');
            if (micButton) micButton.textContent = '🎤 Escuchando...';
        } else {
            waves.forEach(wave => wave.style.animationPlayState = 'paused');
            if (micButton) micButton.textContent = '🎤 Empezar a Hablar';
        }
    }

    showVoiceFeedback(message, type) {
        const feedback = document.getElementById('voice-feedback');
        if (!feedback) return;

        feedback.innerHTML = `
            <div class="feedback-message feedback-${type}">
                ${message}
            </div>
        `;
    }
}

// Sistema de trazado de letras
class LetterTracingSystem {
    constructor() {
        this.canvas = null;
        this.ctx = null;
        this.isDrawing = false;
        this.currentLetter = 'A';
        this.showGuide = false;
        this.strokes = [];
    }

    init(canvasId) {
        this.canvas = document.getElementById(canvasId);
        if (!this.canvas) return;

        this.ctx = this.canvas.getContext('2d');
        this.setupCanvas();
        this.bindEvents();
    }

    setupCanvas() {
        const rect = this.canvas.getBoundingClientRect();
        this.canvas.width = rect.width;
        this.canvas.height = rect.height;
        
        this.ctx.lineWidth = 8;
        this.ctx.lineCap = 'round';
        this.ctx.lineJoin = 'round';
        this.ctx.strokeStyle = '#4caf50';
    }

    bindEvents() {
        // Eventos de mouse
        this.canvas.addEventListener('mousedown', (e) => this.startDrawing(e));
        this.canvas.addEventListener('mousemove', (e) => this.draw(e));
        this.canvas.addEventListener('mouseup', () => this.stopDrawing());
        this.canvas.addEventListener('mouseout', () => this.stopDrawing());

        // Eventos táctiles
        this.canvas.addEventListener('touchstart', (e) => {
            e.preventDefault();
            this.startDrawing(e.touches[0]);
        });
        this.canvas.addEventListener('touchmove', (e) => {
            e.preventDefault();
            this.draw(e.touches[0]);
        });
        this.canvas.addEventListener('touchend', (e) => {
            e.preventDefault();
            this.stopDrawing();
        });
    }

    startDrawing(e) {
        this.isDrawing = true;
        const pos = this.getPosition(e);
        this.ctx.beginPath();
        this.ctx.moveTo(pos.x, pos.y);
        
        this.strokes.push([{x: pos.x, y: pos.y}]);
    }

    draw(e) {
        if (!this.isDrawing) return;
        
        const pos = this.getPosition(e);
        this.ctx.lineTo(pos.x, pos.y);
        this.ctx.stroke();
        
        // Agregar punto al trazo actual
        const currentStroke = this.strokes[this.strokes.length - 1];
        currentStroke.push({x: pos.x, y: pos.y});
    }

    stopDrawing() {
        if (!this.isDrawing) return;
        this.isDrawing = false;
        this.ctx.beginPath();
    }

    getPosition(e) {
        const rect = this.canvas.getBoundingClientRect();
        return {
            x: e.clientX - rect.left,
            y: e.clientY - rect.top
        };
    }

    clear() {
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
        this.strokes = [];
        if (this.showGuide) {
            this.drawGuide();
        }
    }

    drawGuide() {
        this.ctx.save();
        this.ctx.strokeStyle = 'rgba(200, 200, 200, 0.5)';
        this.ctx.lineWidth = 4;
        this.ctx.setLineDash([10, 10]);
        
        // Dibujar guía de la letra
        this.drawLetterGuide(this.currentLetter);
        
        this.ctx.restore();
    }

    drawLetterGuide(letter) {
        const centerX = this.canvas.width / 2;
        const centerY = this.canvas.height / 2;
        const size = Math.min(this.canvas.width, this.canvas.height) * 0.6;
        
        this.ctx.font = `${size}px Arial`;
        this.ctx.textAlign = 'center';
        this.ctx.textBaseline = 'middle';
        this.ctx.strokeText(letter, centerX, centerY);
    }

    toggleGuide() {
        this.showGuide = !this.showGuide;
        this.clear();
    }

    setLetter(letter) {
        this.currentLetter = letter;
        this.clear();
    }

    checkTracing() {
        // Evaluación simple del trazado
        const totalPoints = this.strokes.reduce((sum, stroke) => sum + stroke.length, 0);
        
        if (totalPoints < 10) {
            return { stars: 0, message: 'Intenta trazar más la letra' };
        } else if (totalPoints < 30) {
            return { stars: 1, message: '¡Buen intento!' };
        } else if (totalPoints < 60) {
            return { stars: 2, message: '¡Muy bien!' };
        } else {
            return { stars: 3, message: '¡Excelente trazado!' };
        }
    }
}

// Sistema de mini-juegos
class MiniGameSystem {
    constructor() {
        this.currentGame = null;
        this.gameContainer = null;
    }

    init(containerId) {
        this.gameContainer = document.getElementById(containerId);
    }

    startGame(type, letter) {
        if (!this.gameContainer) return;

        this.currentGame = type;
        this.gameContainer.innerHTML = '';

        switch (type) {
            case 'falling-letters':
                this.createFallingLettersGame(letter);
                break;
            case 'memory':
                this.createMemoryGame(letter);
                break;
            case 'matching':
                this.createMatchingGame(letter);
                break;
        }
    }

    createFallingLettersGame(targetLetter) {
        const gameArea = document.createElement('div');
        gameArea.className = 'falling-letters';
        gameArea.innerHTML = `
            <div class="game-score">Encuentra todas las "${targetLetter}": <span id="score">0</span>/5</div>
        `;
        
        this.gameContainer.appendChild(gameArea);

        let score = 0;
        const target = 5;
        const letters = this.generateRandomLetters(targetLetter, 15);
        
        letters.forEach((letter, index) => {
            setTimeout(() => {
                this.createFallingLetter(letter, targetLetter, gameArea, (isCorrect) => {
                    if (isCorrect) {
                        score++;
                        document.getElementById('score').textContent = score;
                        
                        if (score >= target) {
                            this.endGame(3, '¡Completaste el juego!');
                        }
                    }
                });
            }, index * 1000);
        });
    }

    createFallingLetter(letter, target, container, callback) {
        const letterEl = document.createElement('div');
        letterEl.className = 'falling-letter';
        letterEl.textContent = letter;
        letterEl.style.left = Math.random() * 80 + '%';
        letterEl.style.top = '0px';
        
        if (letter === target) {
            letterEl.classList.add('target');
        }
        
        letterEl.addEventListener('click', () => {
            if (letter === target) {
                letterEl.style.color = '#4caf50';
                callback(true);
            } else {
                letterEl.style.color = '#f44336';
                callback(false);
            }
            
            setTimeout(() => letterEl.remove(), 300);
        });
        
        container.appendChild(letterEl);
        
        // Animación de caída
        let position = 0;
        const fall = setInterval(() => {
            position += 2;
            letterEl.style.top = position + 'px';
            
            if (position > container.offsetHeight) {
                clearInterval(fall);
                letterEl.remove();
            }
        }, 50);
    }

    createMemoryGame(letter) {
        const words = LETTER_DATA[letter].words.slice(0, 3);
        const cards = [...words, ...words]; // Duplicar para pares
        this.shuffleArray(cards);
        
        const gameArea = document.createElement('div');
        gameArea.className = 'memory-game';
        
        let flippedCards = [];
        let matches = 0;
        
        cards.forEach((word, index) => {
            const card = document.createElement('div');
            card.className = 'memory-card';
            card.dataset.word = word;
            card.textContent = '?';
            
            card.addEventListener('click', () => {
                if (card.classList.contains('flipped') || flippedCards.length >= 2) return;
                
                card.classList.add('flipped');
                card.textContent = word;
                flippedCards.push(card);
                
                if (flippedCards.length === 2) {
                    setTimeout(() => {
                        if (flippedCards[0].dataset.word === flippedCards[1].dataset.word) {
                            flippedCards.forEach(c => c.classList.add('matched'));
                            matches++;
                            
                            if (matches === words.length) {
                                this.endGame(3, '¡Encontraste todos los pares!');
                            }
                        } else {
                            flippedCards.forEach(c => {
                                c.classList.remove('flipped');
                                c.textContent = '?';
                            });
                        }
                        flippedCards = [];
                    }, 1000);
                }
            });
            
            gameArea.appendChild(card);
        });
        
        this.gameContainer.appendChild(gameArea);
    }

    createMatchingGame(letter) {
        const words = LETTER_DATA[letter].words;
        const gameArea = document.createElement('div');
        gameArea.className = 'matching-game';
        gameArea.innerHTML = `
            <h3>Conecta la letra con su palabra</h3>
            <div class="matching-content">
                <div class="letters-column">
                    <div class="match-item letter-item" data-letter="${letter}">${letter}</div>
                </div>
                <div class="words-column">
                    ${words.map(word => `
                        <div class="match-item word-item" data-word="${word}">${word}</div>
                    `).join('')}
                </div>
            </div>
        `;
        
        this.gameContainer.appendChild(gameArea);
        
        // Lógica de matching
        let selectedLetter = null;
        let matches = 0;
        
        gameArea.querySelectorAll('.match-item').forEach(item => {
            item.addEventListener('click', () => {
                if (item.classList.contains('letter-item')) {
                    selectedLetter = item;
                    item.classList.add('selected');
                } else if (selectedLetter && item.classList.contains('word-item')) {
                    matches++;
                    item.classList.add('matched');
                    selectedLetter.classList.add('matched');
                    
                    if (matches >= words.length) {
                        this.endGame(2, '¡Excelente!');
                    }
                    
                    selectedLetter = null;
                }
            });
        });
    }

    generateRandomLetters(target, count) {
        const letters = Object.keys(LETTER_DATA);
        const result = [];
        
        // Asegurar que aparezca el target al menos 5 veces
        for (let i = 0; i < 5; i++) {
            result.push(target);
        }
        
        // Llenar el resto con letras aleatorias
        for (let i = 5; i < count; i++) {
            const randomLetter = letters[Math.floor(Math.random() * letters.length)];
            result.push(randomLetter);
        }
        
        this.shuffleArray(result);
        return result;
    }

    shuffleArray(array) {
        for (let i = array.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [array[i], array[j]] = [array[j], array[i]];
        }
    }

    endGame(stars, message) {
        setTimeout(() => {
            showCelebration(message, stars);
            progressSystem.addStars(APP_CONFIG.currentLetter, stars);
        }, 500);
    }
}

// Instancias globales de los sistemas
const progressSystem = new ProgressSystem();
const ttsSystem = new TTSSystem();
const voiceSystem = new VoiceRecognitionSystem();
const tracingSystem = new LetterTracingSystem();
const miniGameSystem = new MiniGameSystem();

// Funciones de navegación
function showScreen(screenId) {
    document.querySelectorAll('.screen').forEach(screen => {
        screen.classList.remove('active');
    });
    document.getElementById(screenId).classList.add('active');
}

function generateLettersGrid() {
    const grid = document.getElementById('letters-grid');
    if (!grid) return;

    const currentLevel = APP_CONFIG.currentLevel;
    const letters = Object.keys(LETTER_DATA).filter(
        letter => LETTER_DATA[letter].level <= currentLevel
    );

    grid.innerHTML = '';

    letters.forEach(letter => {
        const letterData = LETTER_DATA[letter];
        const progress = progressSystem.data.letterProgress[letter];
        
        const card = document.createElement('div