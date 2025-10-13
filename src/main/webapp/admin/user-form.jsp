<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.powergrid.management.model.User" %>
<%@ page import="com.powergrid.management.model.Role" %>
<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser == null || !currentUser.isAdmin()) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    User editUser = (User) request.getAttribute("editUser");
    boolean isEdit = editUser != null;
    Role[] allRoles = (Role[]) request.getAttribute("roles");
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= isEdit ? "Редактирование" : "Добавление" %> пользователя</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .form-container {
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
            width: 100%;
            max-width: 600px;
        }

        .form-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
            padding-bottom: 15px;
            border-bottom: 1px solid #eee;
        }

        .form-header h1 {
            color: #333;
            margin: 0;
        }

        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            text-align: center;
            font-size: 14px;
            transition: all 0.3s ease;
        }

        .btn-primary {
            background: #007bff;
            color: white;
        }

        .btn-primary:hover {
            background: #0056b3;
        }

        .btn-secondary {
            background: #6c757d;
            color: white;
        }

        .btn-secondary:hover {
            background: #545b62;
        }

        .btn-warning {
            background: #ffc107;
            color: #212529;
        }

        .btn-warning:hover {
            background: #e0a800;
        }

        .btn-sm {
            padding: 6px 12px;
            font-size: 12px;
        }

        .form-group {
            margin-bottom: 20px;
        }

        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: 600;
            color: #333;
        }

        .form-control {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
        }

        .form-control:focus {
            outline: none;
            border-color: #007bff;
            box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
        }

        .password-input-container {
            position: relative;
        }

        .password-toggle {
            position: absolute;
            right: 10px;
            top: 50%;
            transform: translateY(-50%);
            background: none;
            border: none;
            cursor: pointer;
            color: #666;
            font-size: 16px;
        }

        .password-toggle:hover {
            color: #333;
        }

        .password-match {
            margin-top: 5px;
            font-size: 12px;
            display: flex;
            align-items: center;
            gap: 5px;
        }

        .match-valid {
            color: #28a745;
        }

        .match-invalid {
            color: #dc3545;
        }

        .password-strength {
            margin-top: 5px;
            font-size: 12px;
            display: flex;
            align-items: center;
            gap: 5px;
        }

        .strength-weak { color: #dc3545; }
        .strength-medium { color: #ffc107; }
        .strength-strong { color: #28a745; }
        .strength-very-strong { color: #20c997; }

        .password-requirements {
            background: #f8f9fa;
            padding: 10px;
            border-radius: 5px;
            margin-top: 10px;
            font-size: 12px;
        }

        .requirement {
            display: flex;
            align-items: center;
            gap: 5px;
            margin-bottom: 3px;
        }

        .requirement.valid {
            color: #28a745;
        }

        .requirement.invalid {
            color: #6c757d;
        }

        .checkbox-group {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .roles-group {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 10px;
            margin-top: 10px;
        }

        .role-checkbox {
            display: flex;
            align-items: center;
            gap: 5px;
        }

        .form-actions {
            display: flex;
            gap: 10px;
            justify-content: flex-end;
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px solid #eee;
        }

        .alert {
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
        }

        .alert-error {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }

        .alert-info {
            background: #d1ecf1;
            color: #0c5460;
            border: 1px solid #bee5eb;
        }

        .password-generator {
            background: #f8f9fa;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            border-left: 4px solid #007bff;
        }

        .password-options {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
            gap: 10px;
            margin: 10px 0;
        }

        .option-group {
            display: flex;
            align-items: center;
            gap: 5px;
        }

        .generated-password {
            background: white;
            padding: 10px;
            border-radius: 5px;
            font-family: monospace;
            font-size: 16px;
            margin: 10px 0;
            border: 1px solid #ddd;
            display: none;
        }

        .password-actions {
            display: flex;
            gap: 5px;
            margin-top: 10px;
        }

        .strength-bar {
            height: 4px;
            border-radius: 2px;
            margin-top: 5px;
            background: #e9ecef;
            overflow: hidden;
        }

        .strength-bar-fill {
            height: 100%;
            transition: all 0.3s ease;
        }

        .bar-weak { background: #dc3545; width: 25%; }
        .bar-medium { background: #ffc107; width: 50%; }
        .bar-strong { background: #28a745; width: 75%; }
        .bar-very-strong { background: #20c997; width: 100%; }
    </style>
</head>
<body>
<div class="form-container">
    <div class="form-header">
        <h1><%= isEdit ? "✏️ Редактирование пользователя" : "➕ Добавление пользователя" %></h1>
        <a href="<%= request.getContextPath() %>/admin/dashboard" class="btn btn-secondary">← Назад</a>
    </div>

    <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-error">
        ❌ <%= request.getAttribute("error") %>
    </div>
    <% } %>

    <!-- Генератор паролей -->
    <div class="password-generator">
        <h4 style="margin-bottom: 10px; color: #333;">🔑 Генератор паролей</h4>
        <div class="password-options">
            <div class="option-group">
                <label for="passwordLength">Длина:</label>
                <input type="number" id="passwordLength" value="12" min="8" max="20"
                       style="width: 60px; padding: 5px; border: 1px solid #ddd; border-radius: 3px;">
            </div>
            <div class="option-group">
                <input type="checkbox" id="includeUppercase" checked>
                <label for="includeUppercase">A-Z</label>
            </div>
            <div class="option-group">
                <input type="checkbox" id="includeLowercase" checked>
                <label for="includeLowercase">a-z</label>
            </div>
            <div class="option-group">
                <input type="checkbox" id="includeNumbers" checked>
                <label for="includeNumbers">0-9</label>
            </div>
            <div class="option-group">
                <input type="checkbox" id="includeSymbols">
                <label for="includeSymbols">!@#$</label>
            </div>
        </div>
        <button onclick="generatePassword()" class="btn btn-warning btn-sm">Сгенерировать пароль</button>

        <div id="generatedPassword" class="generated-password"></div>
        <div id="passwordActions" class="password-actions" style="display: none;">
            <button onclick="useGeneratedPassword()" class="btn btn-primary btn-sm">Использовать пароль</button>
            <button onclick="copyGeneratedPassword()" class="btn btn-secondary btn-sm">Копировать</button>
        </div>
    </div>

    <form method="post" action="<%= request.getContextPath() %>/admin/dashboard" id="userForm">
        <% if (isEdit) { %>
        <input type="hidden" name="id" value="<%= editUser.getId() %>">
        <input type="hidden" name="action" value="update">
        <% } else { %>
        <input type="hidden" name="action" value="create">
        <% } %>

        <div class="form-group">
            <label for="username">Логин *</label>
            <input type="text" id="username" name="username" class="form-control"
                   value="<%= isEdit ? editUser.getUsername() : "" %>" required>
        </div>

        <div class="form-group">
            <label for="fullName">ФИО *</label>
            <input type="text" id="fullName" name="fullName" class="form-control"
                   value="<%= isEdit ? editUser.getFullName() : "" %>" required>
        </div>

        <div class="form-group">
            <label for="email">Email</label>
            <input type="email" id="email" name="email" class="form-control"
                   value="<%= isEdit && editUser.getEmail() != null ? editUser.getEmail() : "" %>">
        </div>

        <% if (!isEdit) { %>
        <div class="form-group">
            <label for="password">Пароль *</label>
            <div class="password-input-container">
                <input type="password" id="password" name="password" class="form-control" required
                       oninput="validatePassword()">
                <button type="button" class="password-toggle" onclick="togglePasswordVisibility('password')">
                    👁️
                </button>
            </div>
            <div class="strength-bar">
                <div id="strengthBarFill" class="strength-bar-fill"></div>
            </div>
            <div id="passwordStrength" class="password-strength"></div>

            <div class="password-requirements">
                <div id="reqLength" class="requirement invalid">✓ Минимум 6 символов</div>
                <div id="reqLetter" class="requirement invalid">✓ Содержит буквы</div>
                <div id="reqDigit" class="requirement invalid">✓ Содержит цифры</div>
            </div>
        </div>

        <div class="form-group">
            <label for="confirmPassword">Подтверждение пароля *</label>
            <div class="password-input-container">
                <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" required
                       oninput="checkPasswordMatch()">
                <button type="button" class="password-toggle" onclick="togglePasswordVisibility('confirmPassword')">
                    👁️
                </button>
            </div>
            <div id="passwordMatchMessage" class="password-match"></div>
        </div>
        <% } else { %>
        <div class="form-group">
            <label for="password">Новый пароль (оставьте пустым, чтобы не менять)</label>
            <div class="password-input-container">
                <input type="password" id="password" name="password" class="form-control"
                       oninput="validatePassword()">
                <button type="button" class="password-toggle" onclick="togglePasswordVisibility('password')">
                    👁️
                </button>
            </div>
            <div class="strength-bar">
                <div id="strengthBarFill" class="strength-bar-fill"></div>
            </div>
            <div id="passwordStrength" class="password-strength"></div>

            <div class="password-requirements">
                <div id="reqLength" class="requirement invalid">✓ Минимум 6 символов</div>
                <div id="reqLetter" class="requirement invalid">✓ Содержит буквы</div>
                <div id="reqDigit" class="requirement invalid">✓ Содержит цифры</div>
            </div>
        </div>

        <div class="form-group">
            <label for="confirmPassword">Подтверждение нового пароля</label>
            <div class="password-input-container">
                <input type="password" id="confirmPassword" name="confirmPassword" class="form-control"
                       oninput="checkPasswordMatch()">
                <button type="button" class="password-toggle" onclick="togglePasswordVisibility('confirmPassword')">
                    👁️
                </button>
            </div>
            <div id="passwordMatchMessage" class="password-match"></div>
        </div>
        <% } %>

        <div class="form-group">
            <label>Роли</label>
            <div class="roles-group">
                <% for (Role role : allRoles) {
                    boolean isChecked = isEdit && editUser.getRoles() != null && editUser.getRoles().contains(role);
                %>
                <div class="role-checkbox">
                    <input type="checkbox" id="role-<%= role.name() %>" name="roles"
                           value="<%= role.name() %>" <%= isChecked ? "checked" : "" %>>
                    <label for="role-<%= role.name() %>"><%= role.name() %></label>
                </div>
                <% } %>
            </div>
        </div>

        <div class="form-group">
            <div class="checkbox-group">
                <input type="checkbox" id="active" name="active"
                    <%= isEdit ? (editUser.isActive() ? "checked" : "") : "checked" %>>
                <label for="active">Активный пользователь</label>
            </div>
        </div>

        <div class="form-actions">
            <a href="<%= request.getContextPath() %>/admin/dashboard" class="btn btn-secondary">Отмена</a>
            <button type="submit" class="btn btn-primary" id="submitButton">
                <%= isEdit ? "💾 Сохранить" : "➕ Создать" %>
            </button>
        </div>
    </form>
</div>

<script>
    // Функция переключения видимости пароля
    function togglePasswordVisibility(fieldId) {
        const passwordField = document.getElementById(fieldId);
        const toggleButton = passwordField.nextElementSibling;

        if (passwordField.type === 'password') {
            passwordField.type = 'text';
            toggleButton.textContent = '🔒';
        } else {
            passwordField.type = 'password';
            toggleButton.textContent = '👁️';
        }
    }

    // Валидация сложности пароля
    function validatePassword() {
        const password = document.getElementById('password').value;
        const strengthElement = document.getElementById('passwordStrength');
        const barFill = document.getElementById('strengthBarFill');

        // Сбрасываем стили
        strengthElement.className = 'password-strength';
        barFill.className = 'strength-bar-fill';

        if (!password) {
            strengthElement.innerHTML = '';
            barFill.style.width = '0';
            updateRequirements(false, false, false);
            return false;
        }

        // Проверяем требования
        const hasMinLength = password.length >= 6;
        const hasLetter = /[a-zA-Z]/.test(password);
        const hasDigit = /[0-9]/.test(password);

        updateRequirements(hasMinLength, hasLetter, hasDigit);

        // Вычисляем силу пароля (по аналогии с Java-методом)
        let strength = 0;

        // Длина пароля
        if (password.length >= 6) strength += 40;
        if (password.length >= 8) strength += 30;
        if (password.length >= 12) strength += 20;

        // Сложность
        if (/[A-Z]/.test(password) && /[a-z]/.test(password)) strength += 10;
        if (hasDigit) strength += 10;
        if (/[^A-Za-z0-9]/.test(password)) strength += 10;

        strength = Math.min(strength, 100);

        // Обновляем визуализацию
        updateStrengthVisualization(strength, hasMinLength, hasLetter, hasDigit);

        return hasMinLength && hasLetter;
    }

    function updateRequirements(hasLength, hasLetter, hasDigit) {
        document.getElementById('reqLength').className = hasLength ? 'requirement valid' : 'requirement invalid';
        document.getElementById('reqLetter').className = hasLetter ? 'requirement valid' : 'requirement invalid';
        document.getElementById('reqDigit').className = hasDigit ? 'requirement valid' : 'requirement invalid';
    }

    function updateStrengthVisualization(strength, hasLength, hasLetter, hasDigit) {
        const strengthElement = document.getElementById('passwordStrength');
        const barFill = document.getElementById('strengthBarFill');

        let strengthText = '';
        let strengthClass = '';
        let barClass = '';

        if (strength < 40 || !hasLength || !hasLetter) {
            strengthText = '❌ Слабый пароль';
            strengthClass = 'strength-weak';
            barClass = 'bar-weak';
        } else if (strength < 70) {
            strengthText = '⚠️ Средний пароль';
            strengthClass = 'strength-medium';
            barClass = 'bar-medium';
        } else if (strength < 90) {
            strengthText = '✅ Хороший пароль';
            strengthClass = 'strength-strong';
            barClass = 'bar-strong';
        } else {
            strengthText = '🔥 Отличный пароль!';
            strengthClass = 'strength-very-strong';
            barClass = 'bar-very-strong';
        }

        strengthElement.innerHTML = `${strengthText} (${strength}%)`;
        strengthElement.className = `password-strength ${strengthClass}`;
        barFill.className = `strength-bar-fill ${barClass}`;
    }

    // Функция проверки совпадения паролей
    function checkPasswordMatch() {
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        const messageElement = document.getElementById('passwordMatchMessage');
        const submitButton = document.getElementById('submitButton');

        // Если оба поля пустые и это режим редактирования - не показываем сообщение
        if (!password && !confirmPassword && <%= isEdit %>) {
            messageElement.innerHTML = '';
            submitButton.disabled = false;
            return;
        }

        if (password && confirmPassword) {
            if (password === confirmPassword) {
                messageElement.innerHTML = '✅ Пароли совпадают';
                messageElement.className = 'password-match match-valid';
                submitButton.disabled = false;
            } else {
                messageElement.innerHTML = '❌ Пароли не совпадают';
                messageElement.className = 'password-match match-invalid';
                submitButton.disabled = true;
            }
        } else if (password || confirmPassword) {
            messageElement.innerHTML = '⚠️ Введите пароль в оба поля';
            messageElement.className = 'password-match match-invalid';
            submitButton.disabled = true;
        } else {
            messageElement.innerHTML = '';
            submitButton.disabled = false;
        }
    }

    // Функция генерации пароля
    function generatePassword() {
        const length = parseInt(document.getElementById('passwordLength').value);
        const includeUppercase = document.getElementById('includeUppercase').checked;
        const includeLowercase = document.getElementById('includeLowercase').checked;
        const includeNumbers = document.getElementById('includeNumbers').checked;
        const includeSymbols = document.getElementById('includeSymbols').checked;

        let charset = '';
        if (includeUppercase) charset += 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
        if (includeLowercase) charset += 'abcdefghijklmnopqrstuvwxyz';
        if (includeNumbers) charset += '0123456789';
        if (includeSymbols) charset += '!@#$%^&*()_+-=[]{}|;:,.<>?';

        // Если ни один чекбокс не выбран, используем все символы
        if (!charset) {
            charset = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
        }

        // Гарантируем минимальную длину
        const actualLength = Math.max(length, 8);
        let password = '';

        // Гарантируем выполнение требований
        if (includeUppercase) password += 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'[Math.floor(Math.random() * 26)];
        if (includeLowercase) password += 'abcdefghijklmnopqrstuvwxyz'[Math.floor(Math.random() * 26)];
        if (includeNumbers) password += '0123456789'[Math.floor(Math.random() * 10)];
        if (includeSymbols) password += '!@#$%^&*()_+-=[]{}|;:,.<>?'[Math.floor(Math.random() * 20)];

        // Заполняем оставшуюся длину
        for (let i = password.length; i < actualLength; i++) {
            password += charset.charAt(Math.floor(Math.random() * charset.length));
        }

        // Перемешиваем символы
        password = shuffleString(password);

        const passwordElement = document.getElementById('generatedPassword');
        const actionsElement = document.getElementById('passwordActions');

        passwordElement.textContent = password;
        passwordElement.style.display = 'block';
        actionsElement.style.display = 'flex';
    }

    function shuffleString(input) {
        const characters = input.split('');
        for (let i = characters.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [characters[i], characters[j]] = [characters[j], characters[i]];
        }
        return characters.join('');
    }

    // Функция использования сгенерированного пароля
    function useGeneratedPassword() {
        const generatedPassword = document.getElementById('generatedPassword').textContent;
        document.getElementById('password').value = generatedPassword;
        document.getElementById('confirmPassword').value = generatedPassword;

        // Запускаем валидацию
        validatePassword();
        checkPasswordMatch();

        // Скрываем сгенерированный пароль
        document.getElementById('generatedPassword').style.display = 'none';
        document.getElementById('passwordActions').style.display = 'none';

        alert('✅ Пароль применен к форме');
    }

    // Функция копирования сгенерированного пароля
    function copyGeneratedPassword() {
        const generatedPassword = document.getElementById('generatedPassword').textContent;
        navigator.clipboard.writeText(generatedPassword).then(() => {
            alert('✅ Пароль скопирован в буфер обмена');
        });
    }

    // Валидация формы при отправке
    document.getElementById('userForm').addEventListener('submit', function(e) {
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        const isEditMode = <%= isEdit %>;

        // Для режима создания проверяем обязательность пароля
        if (!isEditMode) {
            if (!password || !confirmPassword) {
                e.preventDefault();
                alert('❌ Пожалуйста, введите и подтвердите пароль');
                return;
            }

            // Проверяем сложность пароля
            const hasMinLength = password.length >= 6;
            const hasLetter = /[a-zA-Z]/.test(password);

            if (!hasMinLength || !hasLetter) {
                e.preventDefault();
                alert('❌ Пароль не соответствует требованиям безопасности:\n\n• Минимум 6 символов\n• Должен содержать буквы');
                return;
            }
        }

        // Проверяем совпадение паролей (если они введены)
        if (password && confirmPassword && password !== confirmPassword) {
            e.preventDefault();
            alert('❌ Пароли не совпадают');
            return;
        }
    });

    // Инициализация при загрузке страницы
    document.addEventListener('DOMContentLoaded', function() {
        validatePassword();
        checkPasswordMatch();
    });
</script>
</body>
</html>