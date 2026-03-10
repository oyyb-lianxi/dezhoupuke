<template>
  <div class="main-hall">
    <!-- 顶部个人信息栏 -->
    <div class="top-bar">
      <div class="user-info">
        <img src="https://via.placeholder.com/60" alt="头像" class="avatar">
        <div class="user-details">
          <span class="nickname">昵称: 赌神小王</span>
          <span class="level">等级: Lv.7 宗师</span>
          <span class="points">仓库: 125W 总积分: 8500</span>
        </div>
      </div>
      <div class="top-buttons">
        <button class="btn btn-secondary" @click="goToSettings">设置</button>
        <button class="btn btn-danger" @click="logout">退出</button>
      </div>
    </div>

    <!-- 中部桌台列表 -->
    <div class="table-list">
      <h2 class="section-title">选择桌台</h2>
      <div class="table-cards">
        <div 
          v-for="table in tables" 
          :key="table.id" 
          class="table-card" 
          :class="table.status"
          @click="showJoinModal(table)"
        >
          <h3 class="table-name">桌台 {{ table.id }}</h3>
          <div class="table-status">{{ table.statusText }}</div>
          <div class="table-info">
            <div class="player-count">{{ table.players }}/9人</div>
            <div class="blind-info">盲注: {{ table.smallBlind }}W/{{ table.bigBlind }}W</div>
            <div class="time-info">{{ table.timeInfo }}</div>
          </div>
          <button class="btn btn-primary table-btn" :disabled="table.status === 'full'">
            {{ table.status === 'full' ? '观战' : '立即加入' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 底部功能入口 -->
    <div class="bottom-bar">
      <button class="btn btn-secondary function-btn" @click="goToLeaderboard">
        🏆 排行榜
      </button>
      <button class="btn btn-secondary function-btn" @click="goToExchange">
        🎁 积分兑换
      </button>
      <button class="btn btn-secondary function-btn" @click="goToStats">
        📜 个人战绩
      </button>
      <button class="btn btn-secondary function-btn" @click="goToSettings">
        ⚙️ 系统设置
      </button>
    </div>

    <!-- 加入桌台弹窗 -->
    <div v-if="showJoinModalFlag" class="modal">
      <div class="modal-content">
        <h3>加入 桌台 {{ selectedTable.id }}</h3>
        <div class="modal-body">
          <p>当前仓库余额: 125W</p>
          <div class="input-group">
            <label>设置带入积分:</label>
            <input type="number" v-model="buyInAmount" class="input" min="40" max="400">
            <span>W</span>
          </div>
          <p class="range-info">范围: 40W - 400W</p>
          <button class="btn btn-secondary" @click="setMaximumBuyIn">一键最大</button>
          <div class="modal-actions">
            <button class="btn btn-primary" @click="joinTable">确认加入</button>
            <button class="btn btn-secondary" @click="closeJoinModal">取消</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const showJoinModalFlag = ref(false)
const selectedTable = ref(null)
const buyInAmount = ref(40)

// 模拟桌台数据
const tables = ref([
  {
    id: '01',
    status: 'playing',
    statusText: '比赛中',
    players: 6,
    smallBlind: 2,
    bigBlind: 4,
    timeInfo: '剩余: 42分钟'
  },
  {
    id: '02',
    status: 'waiting',
    statusText: '等待中',
    players: 1,
    smallBlind: 1,
    bigBlind: 2,
    timeInfo: '等待开局'
  },
  {
    id: '03',
    status: 'full',
    statusText: '已满员',
    players: 9,
    smallBlind: 5,
    bigBlind: 10,
    timeInfo: '剩余: 15分钟'
  }
])

const showJoinModal = (table) => {
  selectedTable.value = table
  buyInAmount.value = 40
  showJoinModalFlag.value = true
}

const closeJoinModal = () => {
  showJoinModalFlag.value = false
  selectedTable.value = null
}

const setMaximumBuyIn = () => {
  buyInAmount.value = 125 // 仓库余额
}

const joinTable = () => {
  if (selectedTable.value) {
    router.push(`/table/${selectedTable.value.id}`)
  }
}

const goToLeaderboard = () => {
  router.push('/leaderboard')
}

const goToExchange = () => {
  router.push('/exchange')
}

const goToStats = () => {
  // 个人战绩页面
  alert('个人战绩页面开发中')
}

const goToSettings = () => {
  router.push('/settings')
}

const logout = () => {
  router.push('/')
}
</script>

<style scoped>
.main-hall {
  width: 100vw;
  height: 100vh;
  display: flex;
  flex-direction: column;
  padding: 20px;
  gap: 20px;
}

.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: var(--bg-secondary);
  padding: 15px 20px;
  border-radius: var(--border-radius);
  border: 1px solid rgba(255, 215, 0, 0.2);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  border: 2px solid var(--accent-gold);
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.nickname {
  font-size: 18px;
  font-weight: bold;
  color: var(--accent-gold);
}

.level {
  font-size: 14px;
  color: var(--text-secondary);
}

.points {
  font-size: 14px;
  color: var(--text-secondary);
}

.top-buttons {
  display: flex;
  gap: 10px;
}

.table-list {
  flex: 1;
  overflow-y: auto;
}

.section-title {
  color: var(--accent-gold);
  margin-bottom: 20px;
  font-size: 24px;
  text-align: center;
}

.table-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.table-card {
  background-color: var(--bg-secondary);
  border-radius: var(--border-radius);
  padding: 20px;
  border: 2px solid transparent;
  cursor: pointer;
  transition: all 0.3s ease;
}

.table-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
}

.table-card.waiting {
  border-color: var(--accent-green);
}

.table-card.playing {
  border-color: var(--accent-gold);
}

.table-card.full {
  border-color: var(--accent-red);
}

.table-name {
  color: var(--accent-gold);
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
}

.table-status {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 15px;
}

.table-card.waiting .table-status {
  color: var(--accent-green);
}

.table-card.playing .table-status {
  color: var(--accent-gold);
}

.table-card.full .table-status {
  color: var(--accent-red);
}

.table-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 20px;
  font-size: 14px;
  color: var(--text-secondary);
}

.table-btn {
  width: 100%;
}

.bottom-bar {
  display: flex;
  justify-content: space-around;
  background-color: var(--bg-secondary);
  padding: 15px;
  border-radius: var(--border-radius);
  border: 1px solid rgba(255, 215, 0, 0.2);
}

.function-btn {
  flex: 1;
  margin: 0 5px;
}

.input-group {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 15px 0;
}

.input-group input {
  flex: 1;
}

.range-info {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 15px;
}

.modal-actions {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

.modal-actions button {
  flex: 1;
}
</style>
