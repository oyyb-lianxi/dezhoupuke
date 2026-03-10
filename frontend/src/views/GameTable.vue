<template>
  <div class="game-table">
    <!-- 异常状态UI -->
    <!-- 网络断开横幅 -->
    <div v-if="isNetworkDisconnected" class="network-banner">
      网络异常，正在重连...
    </div>

    <!-- 服务器维护遮罩 -->
    <div v-if="isServerMaintenance" class="maintenance-overlay">
      <div class="maintenance-content">
        <h2>系统维护中</h2>
        <p>预计10分钟后恢复</p>
      </div>
    </div>

    <!-- 顶部信息栏 -->
    <div class="top-info-bar">
      <button class="btn btn-secondary back-btn" @click="showLeaveModal">← 返回</button>
      <span class="table-name">桌台02</span>
      <span class="countdown" :class="{ warning: tournamentTime < 300, danger: tournamentTime < 60 }">{{ formatTime(tournamentTime) }}</span>
      <span class="blind-info">💰 盲注2W/4W</span>
      <span v-if="isBlindCapReached" class="blind-cap-warning">已达最大盲注，不再上涨</span>
      <span class="blind-countdown" :class="{ warning: blindTime < 30, danger: blindTime < 10 }">{{ formatTime(blindTime) }}</span>
      <button class="btn btn-secondary menu-btn" @click="toggleMenu">☰</button>
    </div>

    <!-- 牌桌区域 -->
    <div class="table-area">
      <!-- 座位布局 -->
      <div class="seat seat-5">
        <div class="seat-content">
          <img src="https://via.placeholder.com/40" alt="头像" class="seat-avatar">
          <div class="seat-name">玩家5</div>
          <div class="seat-chips">💰 100W</div>
        </div>
      </div>

      <div class="seat seat-4">
        <div class="seat-content">
          <img src="https://via.placeholder.com/40" alt="头像" class="seat-avatar">
          <div class="seat-name">玩家4</div>
          <div class="seat-chips">💰 80W</div>
        </div>
      </div>

      <div class="seat seat-6">
        <div class="seat-content">
          <img src="https://via.placeholder.com/40" alt="头像" class="seat-avatar">
          <div class="seat-name">玩家6</div>
          <div class="seat-chips">💰 150W</div>
        </div>
      </div>

      <div class="seat seat-3">
        <div class="seat-content">
          <img src="https://via.placeholder.com/40" alt="头像" class="seat-avatar">
          <div class="seat-name">玩家3</div>
          <div class="seat-chips">💰 0</div>
          <div class="seat-status">已弃牌</div>
        </div>
      </div>

      <div class="seat seat-7">
        <div class="seat-content">
          <img src="https://via.placeholder.com/40" alt="头像" class="seat-avatar">
          <div class="seat-name">玩家7</div>
          <div class="seat-chips">💰 200W</div>
        </div>
      </div>

      <div class="seat seat-2">
        <div class="seat-content">
          <img src="https://via.placeholder.com/40" alt="头像" class="seat-avatar">
          <div class="seat-name">玩家2</div>
          <div class="seat-chips">💰 50W</div>
        </div>
      </div>

      <div class="seat seat-8">
        <div class="seat-content">
          <img src="https://via.placeholder.com/40" alt="头像" class="seat-avatar">
          <div class="seat-name">玩家8</div>
          <div class="seat-chips">💰 120W</div>
        </div>
      </div>

      <div class="seat seat-1">
        <div class="seat-content">
          <img src="https://via.placeholder.com/40" alt="头像" class="seat-avatar">
          <div class="seat-name">玩家1</div>
          <div class="seat-chips">💰 90W</div>
        </div>
      </div>

      <!-- 公共牌区 -->
      <div class="community-cards">
        <div class="cards">
          <div class="card">♠️A</div>
          <div class="card">♥️K</div>
          <div class="card">♦️Q</div>
          <div class="card card-back">?</div>
          <div class="card card-back">?</div>
        </div>
        <div class="pot">底池: 86W</div>
      </div>

      <!-- 自己的座位 -->
      <div class="seat seat-9 current-player">
        <div class="pending-points">+20W(下局生效)</div>
        <div class="seat-content">
          <img src="https://via.placeholder.com/40" alt="头像" class="seat-avatar">
          <div class="seat-name">赌神小王 <span class="seat-me">(我)</span></div>
          <div class="seat-chips">💰 125W</div>
          <div class="seat-dealer">dealer</div>
        </div>
      </div>
    </div>

    <!-- 底部操作区 -->
    <div class="bottom-action-area">
      <!-- 手牌 -->
      <div class="player-cards">
        <div class="card">♠️A</div>
        <div class="card">♥️A</div>
      </div>

      <!-- 倒计时 -->
      <div class="action-countdown" :class="{ warning: actionTime < 5, danger: actionTime < 3 }">
        <div class="countdown-circle" :style="{ '--progress': (actionTime / 10) * 100 + '%' }"></div>
        <div class="countdown-number">{{ actionTime }}</div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <button class="btn btn-danger action-btn" @click="fold">弃牌</button>
        <button class="btn btn-secondary action-btn" @click="check">过牌 (10s)</button>
        <div class="bet-area">
          <input type="number" v-model="betAmount" class="input bet-input" min="2" max="125">
          <span class="bet-unit">W</span>
        </div>
        <button class="btn btn-primary action-btn" @click="allIn">All-in 125W</button>
      </div>

      <!-- 快捷下注 -->
      <div class="quick-bet">
        <button class="btn btn-secondary quick-bet-btn" @click="setBet(43)">1/2底池</button>
        <button class="btn btn-secondary quick-bet-btn" @click="setBet(86)">底池</button>
        <button class="btn btn-secondary quick-bet-btn" @click="setBet(172)">2x底池</button>
        <button class="btn btn-secondary quick-bet-btn" @click="allIn">All-in</button>
      </div>
    </div>

    <!-- 侧边面板 -->
    <div class="side-panel">
      <div class="panel-header">
        <button class="btn btn-secondary panel-toggle" @click="toggleSidePanel">☰</button>
        <h3>本局历史</h3>
      </div>
      <div class="panel-content" v-if="showSidePanel">
        <div class="history-list">
          <div class="history-item active" @click="showHistoryDetail(5)">第5手 [查看]</div>
          <div class="history-item" @click="showHistoryDetail(4)">第4手 [查看]</div>
          <div class="history-item" @click="showHistoryDetail(3)">第3手 [查看]</div>
          <div class="history-item" @click="showHistoryDetail(2)">第2手 [查看]</div>
          <div class="history-item" @click="showHistoryDetail(1)">第1手 [查看]</div>
        </div>
      </div>
    </div>

    <!-- 离开桌台弹窗 -->
    <div v-if="showLeaveModalFlag" class="modal">
      <div class="modal-content">
        <h3>确定离开当前桌台？</h3>
        <div class="modal-actions">
          <button class="btn btn-primary" @click="leaveTable">确定</button>
          <button class="btn btn-secondary" @click="closeLeaveModal">取消</button>
        </div>
      </div>
    </div>

    <!-- All-in确认弹窗 -->
    <div v-if="showAllInModal" class="modal">
      <div class="modal-content">
        <h3>⚠️ 确认全下？</h3>
        <p>您将下注全部 125W 积分</p>
        <p>确定后不可撤销</p>
        <div class="modal-actions">
          <button class="btn btn-primary" @click="confirmAllIn">确定</button>
          <button class="btn btn-secondary" @click="closeAllInModal">取消</button>
        </div>
      </div>
    </div>

    <!-- 补分弹窗 -->
    <div v-if="showAddPointsModal" class="modal">
      <div class="modal-content">
        <h3>补充积分</h3>
        <p>当前仓库: 85W</p>
        <p>当前桌面: 15W</p>
        <div class="input-group">
          <label>补充数量:</label>
          <input type="number" v-model="addPointsAmount" class="input" min="40" max="85">
          <span>W</span>
        </div>
        <button class="btn btn-secondary" @click="setMaximumAddPoints">一键填入最大值: 85W</button>
        <p class="warning-text">⚠️ 将从仓库扣除，下局生效</p>
        <div class="modal-actions">
          <button class="btn btn-primary" @click="confirmAddPoints">确认</button>
          <button class="btn btn-secondary" @click="closeAddPointsModal">取消</button>
        </div>
      </div>
    </div>

    <!-- 涨盲提醒弹窗 -->
    <div v-if="showBlindIncreaseModal" class="modal">
      <div class="modal-content">
        <h3>📢 盲注升级</h3>
        <p>小盲: 2W → 4W</p>
        <p>大盲: 4W → 8W</p>
        <p class="countdown-text">(3秒后自动关闭)</p>
      </div>
    </div>

    <!-- 亮牌选择弹窗 -->
    <div v-if="showShowCardsModal" class="modal">
      <div class="modal-content">
        <h3>🎉 您赢得了本手牌！ 底池: +120W</h3>
        <p>您的手牌: ♠️A ♥️A (三条A)</p>
        <p>是否向其他玩家展示您的手牌？</p>
        <div class="modal-actions">
          <button class="btn btn-primary" @click="showCards">亮牌展示</button>
          <button class="btn btn-secondary" @click="hideCards">保持神秘</button>
        </div>
      </div>
    </div>

    <!-- 被踢出桌台弹窗 -->
    <div v-if="showKickedOutModal" class="modal">
      <div class="modal-content">
        <h3>⚠️ 您已被管理员请离当前桌台</h3>
        <div class="modal-actions">
          <button class="btn btn-primary" @click="goToHall">确定</button>
        </div>
      </div>
    </div>

    <!-- 账号在其他地方登录弹窗 -->
    <div v-if="showAccountLoggedInElsewhereModal" class="modal">
      <div class="modal-content">
        <h3>⚠️ 账号已在其他地方登录</h3>
        <p>您将被强制退出游戏</p>
        <div class="modal-actions">
          <button class="btn btn-primary" @click="logout">确定</button>
        </div>
      </div>
    </div>

    <!-- 观战模式 -->
    <div v-if="isSpectating" class="spectator-mode">
      <div class="spectator-header">
        <h2>👁️ 观战模式</h2>
        <p>您已被淘汰，本手牌结束后可重新加入</p>
        <p>当前手牌剩余玩家: 6人 底池: 156W</p>
      </div>
      <div class="spectator-content">
        <div class="spectator-cards">
          <p>公共牌: ♠️A ♥️K ♦️Q</p>
          <p>等待转牌...</p>
        </div>
        <div class="spectator-players">
          <div class="spectator-player" v-for="i in 6" :key="i">
            <span>?? ??</span>
            <span>玩家{{ i }}</span>
            <span>💰{{ [100, 80, 0, 150, 200, 50][i-1] }}W</span>
          </div>
        </div>
        <div class="spectator-log">
          <h4>操作日志</h4>
          <p>14:32:15 玩家A 下注 10W</p>
          <p>14:32:08 玩家B 弃牌</p>
        </div>
      </div>
      <div class="spectator-footer">
        <button class="btn btn-secondary" @click="viewHistory">查看历史记录(仅看公共牌)</button>
        <button class="btn btn-primary" @click="rejoin">立即重进(倒计时)</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 状态管理
const showLeaveModalFlag = ref(false)
const showAllInModal = ref(false)
const showAddPointsModal = ref(false)
const showBlindIncreaseModal = ref(false)
const showShowCardsModal = ref(false)
const showKickedOutModal = ref(false)
const showAccountLoggedInElsewhereModal = ref(false)
const showSidePanel = ref(true)
const isSpectating = ref(false)
const isNetworkDisconnected = ref(false)
const isServerMaintenance = ref(false)
const isBlindCapReached = ref(false)

// 倒计时
const tournamentTime = ref(3240) // 54分钟
const blindTime = ref(443) // 7分23秒
const actionTime = ref(10)

// 下注金额
const betAmount = ref(2)
const addPointsAmount = ref(40)

// 定时器
let tournamentTimer = null
let blindTimer = null
let actionTimer = null
let blindIncreaseTimer = null

// 格式化时间
const formatTime = (seconds) => {
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

// 切换侧边面板
const toggleSidePanel = () => {
  showSidePanel.value = !showSidePanel.value
}

// 显示离开弹窗
const showLeaveModal = () => {
  showLeaveModalFlag.value = true
}

// 关闭离开弹窗
const closeLeaveModal = () => {
  showLeaveModalFlag.value = false
}

// 离开桌台
const leaveTable = () => {
  router.push('/hall')
}

// 弃牌
const fold = () => {
  // 模拟弃牌操作
  alert('您已弃牌')
  resetActionTimer()
}

// 过牌
const check = () => {
  // 模拟过牌操作
  alert('您已过牌')
  resetActionTimer()
}

// 全下
const allIn = () => {
  showAllInModal.value = true
}

// 确认全下
const confirmAllIn = () => {
  // 模拟全下操作
  alert('您已全下')
  showAllInModal.value = false
  resetActionTimer()
}

// 关闭全下弹窗
const closeAllInModal = () => {
  showAllInModal.value = false
}

// 设置下注金额
const setBet = (amount) => {
  betAmount.value = amount
}

// 显示补分弹窗
const showAddPoints = () => {
  showAddPointsModal.value = true
}

// 关闭补分弹窗
const closeAddPointsModal = () => {
  showAddPointsModal.value = false
}

// 设置最大补分
const setMaximumAddPoints = () => {
  addPointsAmount.value = 85
}

// 确认补分
const confirmAddPoints = () => {
  // 模拟补分操作
  alert(`您已成功补分 ${addPointsAmount.value}W`)
  showAddPointsModal.value = false
}

// 显示历史详情
const showHistoryDetail = (hand) => {
  // 模拟显示历史详情
  alert(`显示第${hand}手历史详情`)
}

// 亮牌
const showCards = () => {
  // 模拟亮牌操作
  alert('您已选择亮牌')
  showShowCardsModal.value = false
}

// 保持神秘
const hideCards = () => {
  // 模拟不亮牌操作
  alert('您已选择保持神秘')
  showShowCardsModal.value = false
}

// 查看历史记录
const viewHistory = () => {
  // 模拟查看历史记录
  alert('查看历史记录')
}

// 重新加入
const rejoin = () => {
  // 模拟重新加入
  isSpectating.value = false
  alert('您已重新加入比赛')
}

// 切换菜单
const toggleMenu = () => {
  // 模拟切换菜单
  alert('菜单已切换')
}

// 前往大厅
const goToHall = () => {
  router.push('/hall')
}

// 退出登录
const logout = () => {
  router.push('/')
}

// 重置操作定时器
const resetActionTimer = () => {
  if (actionTimer) {
    clearInterval(actionTimer)
  }
  actionTime.value = 10
  startActionTimer()
}

// 开始操作定时器
const startActionTimer = () => {
  actionTimer = setInterval(() => {
    if (actionTime.value > 0) {
      actionTime.value--
    } else {
      // 超时处理
      clearInterval(actionTimer)
      alert('已自动过牌')
      actionTime.value = 10
      startActionTimer()
    }
  }, 1000)
}

// 开始赛事定时器
const startTournamentTimer = () => {
  tournamentTimer = setInterval(() => {
    if (tournamentTime.value > 0) {
      tournamentTime.value--
    } else {
      clearInterval(tournamentTimer)
      alert('比赛时间结束')
    }
  }, 1000)
}

// 开始盲注定时器
const startBlindTimer = () => {
  blindTimer = setInterval(() => {
    if (blindTime.value > 0) {
      blindTime.value--
    } else {
      clearInterval(blindTimer)
      // 涨盲
      showBlindIncreaseModal.value = true
      // 3秒后关闭弹窗
      blindIncreaseTimer = setTimeout(() => {
        showBlindIncreaseModal.value = false
        blindTime.value = 600 // 10分钟
        startBlindTimer()
      }, 3000)
    }
  }, 1000)
}

// 生命周期
onMounted(() => {
  startTournamentTimer()
  startBlindTimer()
  startActionTimer()
})

onUnmounted(() => {
  if (tournamentTimer) clearInterval(tournamentTimer)
  if (blindTimer) clearInterval(blindTimer)
  if (actionTimer) clearInterval(actionTimer)
  if (blindIncreaseTimer) clearTimeout(blindIncreaseTimer)
})
</script>

<style scoped>
.game-table {
  width: 100vw;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: var(--bg-primary);
  position: relative;
}

.top-info-bar {
  height: 80px;
  background-color: var(--bg-secondary);
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 0 20px;
  border-bottom: 1px solid rgba(255, 215, 0, 0.2);
}

.back-btn {
  font-size: 18px;
}

.table-name {
  font-size: 20px;
  font-weight: bold;
  color: var(--accent-gold);
}

.countdown {
  font-size: 24px;
  font-weight: bold;
  font-family: monospace;
}

.blind-info {
  font-size: 18px;
  font-weight: bold;
  color: var(--accent-gold);
}

.blind-countdown {
  font-size: 16px;
  font-weight: bold;
  font-family: monospace;
}

.menu-btn {
  font-size: 18px;
}

.table-area {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  grid-template-rows: repeat(5, 1fr);
  gap: 20px;
  padding: 20px;
  position: relative;
}

.seat {
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.seat-1 {
  grid-column: 3;
  grid-row: 5;
}

.seat-2 {
  grid-column: 2;
  grid-row: 4;
}

.seat-3 {
  grid-column: 1;
  grid-row: 3;
}

.seat-4 {
  grid-column: 2;
  grid-row: 2;
}

.seat-5 {
  grid-column: 3;
  grid-row: 1;
}

.seat-6 {
  grid-column: 4;
  grid-row: 2;
}

.seat-7 {
  grid-column: 5;
  grid-row: 3;
}

.seat-8 {
  grid-column: 4;
  grid-row: 4;
}

.seat-9 {
  grid-column: 3;
  grid-row: 5;
}

.seat-content {
  width: 120px;
  height: 150px;
  background-color: var(--bg-secondary);
  border-radius: var(--border-radius);
  padding: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 2px solid transparent;
  transition: all 0.3s ease;
}

.seat.current-player .seat-content {
  border-color: var(--accent-gold);
  box-shadow: 0 0 10px rgba(255, 215, 0, 0.5);
}

.seat-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-bottom: 10px;
}

.seat-name {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 5px;
  text-align: center;
}

.seat-me {
  color: var(--accent-gold);
  font-size: 12px;
}

.seat-chips {
  font-size: 14px;
  color: var(--accent-gold);
  margin-bottom: 5px;
}

.seat-dealer {
  font-size: 12px;
  color: var(--accent-green);
  background-color: var(--bg-tertiary);
  padding: 2px 8px;
  border-radius: 10px;
}

.seat-status {
  font-size: 12px;
  color: var(--accent-red);
  margin-top: 5px;
}

.pending-points {
  position: absolute;
  top: -10px;
  right: -10px;
  background-color: var(--accent-yellow);
  color: black;
  padding: 5px 10px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: bold;
  z-index: 10;
}

.community-cards {
  grid-column: 2 / 5;
  grid-row: 2 / 5;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: var(--bg-secondary);
  border-radius: var(--border-radius);
  padding: 20px;
  border: 2px solid var(--accent-gold);
}

.cards {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.card {
  width: 80px;
  height: 120px;
  background-color: white;
  color: black;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: bold;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.card-back {
  background: linear-gradient(45deg, #0f3460, #16213e);
  color: white;
}

.pot {
  font-size: 18px;
  font-weight: bold;
  color: var(--accent-gold);
}

.bottom-action-area {
  height: 200px;
  background-color: var(--bg-secondary);
  border-top: 1px solid rgba(255, 215, 0, 0.2);
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.player-cards {
  display: flex;
  gap: 10px;
  justify-content: center;
}

.action-countdown {
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  width: 100px;
  height: 100px;
  margin: 0 auto;
}

.countdown-circle {
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: conic-gradient(
    var(--accent-red) 0% var(--progress),
    var(--accent-yellow) var(--progress) 60%,
    var(--accent-gold) 60% 100%
  );
  display: flex;
  align-items: center;
  justify-content: center;
}

.countdown-circle::before {
  content: '';
  width: 80%;
  height: 80%;
  border-radius: 50%;
  background-color: var(--bg-secondary);
}

.countdown-number {
  position: relative;
  z-index: 1;
  font-size: 24px;
  font-weight: bold;
  font-family: monospace;
}

.action-buttons {
  display: flex;
  gap: 10px;
  align-items: center;
}

.action-btn {
  flex: 1;
}

.bet-area {
  flex: 2;
  display: flex;
  align-items: center;
  gap: 10px;
}

.bet-input {
  flex: 1;
}

.quick-bet {
  display: flex;
  gap: 10px;
}

.quick-bet-btn {
  flex: 1;
}

.side-panel {
  position: absolute;
  top: 80px;
  right: 0;
  width: 300px;
  height: calc(100vh - 280px);
  background-color: var(--bg-secondary);
  border-left: 1px solid rgba(255, 215, 0, 0.2);
  z-index: 100;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px;
  border-bottom: 1px solid rgba(255, 215, 0, 0.2);
}

.panel-header h3 {
  color: var(--accent-gold);
}

.panel-content {
  padding: 15px;
  overflow-y: auto;
  height: calc(100% - 60px);
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.history-item {
  padding: 10px;
  background-color: var(--bg-tertiary);
  border-radius: var(--border-radius);
  cursor: pointer;
  transition: all 0.3s ease;
}

.history-item:hover {
  background-color: var(--accent-gold);
  color: var(--bg-primary);
}

.history-item.active {
  background-color: var(--accent-gold);
  color: var(--bg-primary);
}

.spectator-mode {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(26, 26, 46, 0.95);
  display: flex;
  flex-direction: column;
  z-index: 200;
}

.spectator-header {
  background-color: var(--bg-secondary);
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid var(--accent-gold);
}

.spectator-header h2 {
  color: var(--accent-gold);
  margin-bottom: 10px;
}

.spectator-content {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.spectator-cards {
  background-color: var(--bg-secondary);
  padding: 20px;
  border-radius: var(--border-radius);
  text-align: center;
}

.spectator-players {
  background-color: var(--bg-secondary);
  padding: 20px;
  border-radius: var(--border-radius);
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.spectator-player {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background-color: var(--bg-tertiary);
  border-radius: var(--border-radius);
}

.spectator-log {
  background-color: var(--bg-secondary);
  padding: 20px;
  border-radius: var(--border-radius);
  flex: 1;
  overflow-y: auto;
}

.spectator-log h4 {
  color: var(--accent-gold);
  margin-bottom: 10px;
}

.spectator-footer {
  background-color: var(--bg-secondary);
  padding: 20px;
  display: flex;
  gap: 10px;
  border-top: 1px solid var(--accent-gold);
}

.spectator-footer button {
  flex: 1;
}

.modal-actions {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

.modal-actions button {
  flex: 1;
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

.warning-text {
  color: var(--accent-yellow);
  margin: 10px 0;
}

.countdown-text {
  color: var(--text-secondary);
  margin-top: 10px;
}

/* 异常状态UI */
.network-banner {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  background-color: var(--accent-red);
  color: white;
  padding: 10px;
  text-align: center;
  font-weight: bold;
  z-index: 1000;
  animation: pulse 2s infinite;
}

.maintenance-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.maintenance-content {
  background-color: var(--bg-secondary);
  padding: 40px;
  border-radius: var(--border-radius);
  text-align: center;
  border: 2px solid var(--accent-gold);
}

.maintenance-content h2 {
  color: var(--accent-gold);
  margin-bottom: 20px;
}

.maintenance-content p {
  color: var(--text-primary);
  font-size: 18px;
}

.blind-cap-warning {
  background-color: var(--accent-yellow);
  color: black;
  padding: 5px 10px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: bold;
}

/* 动画效果 */
@keyframes pulse {
  0% {
    opacity: 1;
  }
  50% {
    opacity: 0.7;
  }
  100% {
    opacity: 1;
  }
}

/* 倒计时视觉效果增强 */
.action-countdown.danger .countdown-circle {
  animation: pulse 1s infinite;
}

.action-countdown.danger .countdown-number {
  font-size: 28px;
  color: var(--accent-red);
}

/* 超时按钮样式 */
.action-btn:disabled {
  background-color: var(--bg-tertiary);
  color: var(--text-secondary);
  cursor: not-allowed;
}
</style>
