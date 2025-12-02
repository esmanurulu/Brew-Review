document.addEventListener("DOMContentLoaded", function() {
    
    // --- Animasyon Kodları (Aynı Kalıyor) ---
    const animationDuration = 4200; 

    setTimeout(function() {
        const intro = document.getElementById('intro-container');
        const login = document.getElementById('login-container');
        const body = document.body;

        if(intro) intro.style.display = 'none'; 
        if(body) body.classList.add('brown-mode');

        if(login) {
            login.style.display = 'block';
            setTimeout(() => {
                login.style.opacity = '1';
            }, 100);
        }
    }, animationDuration);

    // --- GİRİŞ İŞLEMLERİ (DÜZELTİLEN KISIM) ---
    const form = document.getElementById('loginForm');
    
    if(form) {
        form.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const formData = new FormData(form);
            
            // 1. Checkbox'ı bul
            const adminCheckbox = document.getElementById('isAdmin');
            
            // 2. Eğer işaretliyse 'type' parametresini 'admin' olarak ekle
            if (adminCheckbox && adminCheckbox.checked) {
                formData.append('type', 'admin');
                console.log("Yönetici girişi seçildi."); // Tarayıcı konsolunda görmek için
            } else {
                formData.append('type', 'user');
                console.log("Normal kullanıcı girişi.");
            }

            // 3. Sunucuya gönder
            fetch('/login', { 
                method: 'POST', 
                body: formData 
            })
            .then(response => response.json())
            .then(data => {
                if(data.status === 'success') {
                    // Başarılıysa yönlendir
                    window.location.href = data.redirect;
                } else {
                    // Hatalıysa uyarı ver
                    alert(data.message);
                }
            })
            .catch(error => console.error('Hata:', error));
        });
    }
});