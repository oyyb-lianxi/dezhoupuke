<template>
  <div class="login-page">
    <div class="login-container">
      <h1 class="logo">德州扑克积分赛系统</h1>
      <div class="login-form">
        <div class="form-group">
          <label for="username">账号</label>
          <input type="text" id="username" v-model="username" class="input" placeholder="手机号/会员号">
        </div>
        <div class="form-group">
          <label for="password">验证码</label>
          <div class="password-input">
            <input type="text" id="password" v-model="password" class="input" placeholder="输入验证码">
            <button class="btn btn-secondary" @click="getVerificationCode">获取验证码</button>
          </div>
        </div>
        <div class="form-group checkbox-group">
          <input type="checkbox" id="agree" v-model="agree">
          <label for="agree">我已阅读并同意 <a href="#">《用户协议》</a>，且已满18周岁</label>
        </div>
        <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
        <button class="btn btn-primary login-btn" @click="login" :disabled="!agree || loading">
          <span v-if="loading">登录中...</span>
          <span v-else>进入赛场</span>
        </button>
      </div>
      <div class="login-footer">
        <p>版本：v1.1</p>
        <p>未成年人禁止参与</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = ref('')
const password = ref('')
const agree = ref(false)
const loading = ref(false)
const errorMessage = ref('')

const getVerificationCode = () => {
  // 模拟获取验证码
  alert('验证码已发送到您的手机')
}

const login = async () => {
  if (!username.value || !password.value || !agree.value) {
    errorMessage.value = '请填写完整信息并同意用户协议'
    return
  }

  loading.value = true
  errorMessage.value = ''

  try {
    const response = await fetch('/api/user/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        idCard: username.value,
        password: password.value
      })
    })

    const data = await response.json()

    if (data.success) {
      // 登录成功，保存用户信息
      localStorage.setItem('userInfo', JSON.stringify(data))
      router.push('/hall')
    } else {
      errorMessage.value = data.message || '登录失败'
    }
  } catch (error) {
    errorMessage.value = '网络错误，请稍后重试'
    console.error('登录错误:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  width: 100vw;
  height: 100vh;
  background-color: var(--bg-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  background-image: url('data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI2MCIgaGVpZ2h0PSI2MCIgdmlld0JveD0iMCAwIDYwIDYwIj48ZyBmaWxsPSJub25lIiBmaWxsLXJ1bGU9ImV2ZW5vZGQiPjxwYXRoIGQ9Ik0zNiAxOGMzLjMxNCAwIDYtMi42ODYgNi02cy0yLjY4Ni02LTYtNi02IDIuNjg2LTYgNiAyLjY4NiA2IDYgNnptMCAzMGMzLjMxNCAwIDYtMi42ODYgNi02cy0yLjY4Ni02LTYtNi02IDIuNjg2LTYgNiAyLjY4NiA2IDYgNnptLTE4IDBjMy4zMTQgMCA2LTIuNjg2IDYtNnMtMi42ODYtNi02LTYtNiAyLjY4Ni02IDYgMi42ODYgNiA2IDZ6bTAgMzBjMy4zMTQgMCA2LTIuNjg2IDYtNnMtMi42ODYtNi02LTYtNiAyLjY4Ni02IDYgMi42ODYgNiA2IDZ6Ii8+PC9nPjwvc3ZnPg==');
  background-size: 100px;
  animation: float 20s linear infinite;
}

@keyframes float {
  0% {
    background-position: 0 0;
  }
  100% {
    background-position: 100px 100px;
  }
}

.login-container {
  background-color: var(--bg-secondary);
  border-radius: var(--border-radius);
  padding: 40px;
  width: 400px;
  max-width: 90%;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.5);
  border: 1px solid var(--accent-gold);
}

.logo {
  text-align: center;
  color: var(--accent-gold);
  font-size: 24px;
  margin-bottom: 30px;
  text-shadow: 0 0 10px rgba(255, 215, 0, 0.5);
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

label {
  font-size: 14px;
  color: var(--text-secondary);
}

.password-input {
  display: flex;
  gap: 10px;
}

.password-input input {
  flex: 1;
}

.checkbox-group {
  flex-direction: row;
  align-items: center;
  font-size: 14px;
}

.checkbox-group input {
  margin-right: 10px;
}

.checkbox-group a {
  color: var(--accent-gold);
  text-decoration: none;
}

.login-btn {
  margin-top: 20px;
  padding: 15px;
  font-size: 18px;
}

.error-message {
  color: #ff4d4f;
  font-size: 14px;
  margin-top: -10px;
  margin-bottom: 10px;
  text-align: center;
}

.login-footer {
  margin-top: 30px;
  text-align: center;
  font-size: 12px;
  color: var(--text-secondary);
  line-height: 1.5;
}
</style>
