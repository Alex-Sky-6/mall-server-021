<template>
  <div class="wrapper">
    <header class="custom-header">
      <!-- 回退按钮 -->
      <div class="back-arrow" @click="toback()">
        <el-icon><ArrowLeftBold /></el-icon>
      </div>
      <p>商家评论</p>
    </header>

    <!-- 商家信息卡片 -->
    <div class="business-card">
      <div class="business-info">
        <img class="business-logo" :src="business.businessImg" alt="商家logo" />
        <div class="business-details">
          <h3>{{ business.businessName }}</h3>
          <p class="business-address">商家地址：{{ business.businessAddress || '沈阳市浑南区软件园E18栋' }}</p>
        </div>
      </div>
      <div class="business-actions">
        <button class="action-btn">商家商品 ▼</button>
      </div>
    </div>

    <!-- 商品信息卡片 -->
    <div class="product-card" v-if="selectedProduct">
      <img class="product-image" :src="selectedProduct.image" alt="商品图片" />
      <div class="product-info">
        <h4>{{ selectedProduct.name }}</h4>
      </div>
    </div>

    <!-- 评论筛选标签 -->
    <div class="comment-filter">
      <div 
        v-for="filter in filterOptions" 
        :key="filter.key"
        class="filter-item"
        :class="{ active: activeFilter === filter.key }"
        @click="setActiveFilter(filter.key)"
      >
        {{ filter.label }}
      </div>
    </div>

    <!-- 评论列表 -->
    <div class="comment-list">
      <div v-for="comment in filteredComments" :key="comment.id" class="comment-item">
        <!-- 用户信息区域 -->
        <div class="user-info">
          <img class="avatar" :src="comment.userAvatar" alt="用户头像" />
          <div class="user-details">
            <p class="username">{{ comment.username }}</p>
            <p class="comment-date">{{ comment.date }}</p>
          </div>
        </div>
        
        <!-- 评分区域 -->
        <div class="rating">
          <span v-for="n in 5" :key="n" class="star" :class="{ active: n <= comment.rating }">★</span>
          <span class="rating-text">{{ comment.rating }}.0</span>
        </div>
        
        <!-- 评论内容 -->
        <div class="comment-content">
          <p>{{ comment.content }}</p>
        </div>
        
        <!-- 评论图片 -->
        <div class="comment-images" v-if="comment.images && comment.images.length > 0">
          <img v-for="(image, index) in comment.images" :key="index" :src="image" alt="评论图片" class="comment-image" />
        </div>
      </div>
      
      <!-- 空状态 -->
      <div v-if="filteredComments.length === 0" class="empty-state">
        <p>暂无评论</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { get } from '@/api/index.js';
import { ElMessage } from 'element-plus';

const router = useRouter();
const route = useRoute();
const businessId = route.query.businessId;

// 商家信息
const business = ref({
  businessName: '',
  businessImg: '',
  businessAddress: ''
});

// 选中的商品信息
const selectedProduct = ref({
  name: '招牌香酥鸡饼·麻酱千拌',
  image: '/src/assets/businessImg/1.jpg'
});

// 筛选选项
const filterOptions = ref([
  { key: 'all', label: '全部' },
  { key: 'good', label: '好评' },
  { key: 'medium', label: '中评' },
  { key: 'bad', label: '我的评价' }
]);

// 当前激活的筛选项
const activeFilter = ref('all');

// 评论列表数据
const commentList = ref([
  {
    id: 1,
    userAvatar: '/src/assets/businessImg/1.jpg',
    username: '先睡一觉',
    date: '2024-06-21',
    rating: 2,
    content: '味道还不错，是我喜欢的学校边的那种感觉！不过价格有点贵，还是有点贵的，还是有点贵的，10多分钟就送到了，价格也不算贵，还是有点贵的商家的一',
    images: ['/src/assets/businessImg/1.jpg']
  }
]);

// 根据筛选条件过滤评论
const filteredComments = computed(() => {
  if (activeFilter.value === 'all') {
    return commentList.value;
  } else if (activeFilter.value === 'good') {
    return commentList.value.filter(comment => comment.rating >= 4);
  } else if (activeFilter.value === 'medium') {
    return commentList.value.filter(comment => comment.rating === 3);
  } else if (activeFilter.value === 'bad') {
    return commentList.value.filter(comment => comment.rating <= 2);
  }
  return commentList.value;
});

// 设置激活的筛选项
const setActiveFilter = (filterKey) => {
  activeFilter.value = filterKey;
};

// 回退方法
const toback = () => {
  router.back();
};

// 加载商家信息
const loadBusinessInfo = async () => {
  try {
    const res = await get(`/business/info/${businessId}`);
    if (res.data.code === 20000) {
      business.value = res.data.resultdata;
    }
  } catch (error) {
    console.error('加载商家信息失败:', error);
  }
};

// 加载评论数据
const loadComments = async () => {
  try {
    // 这里应该调用获取商家评论的API
    // const res = await get(`/comment/business/${businessId}`);
    // if (res.data.code === 20000) {
    //   commentList.value = res.data.resultdata;
    // }
  } catch (error) {
    console.error('加载评论失败:', error);
  }
};

onMounted(() => {
  if (businessId) {
    loadBusinessInfo();
    loadComments();
  }
});
</script>

<style scoped>
.wrapper {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 20px;
  box-sizing: border-box;
}

/* 顶部标题栏 */
.custom-header {
  width: 100%;
  height: 12vw;
  background: linear-gradient(to right, #fff1eb, #ace0f9);
  color: #596164;
  font-size: 5vw;
  position: fixed;
  left: 0;
  top: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.custom-header p {
  margin: 0;
  letter-spacing: 2vw;
}

.back-arrow {
  position: absolute;
  left: 4vw;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
  font-size: 6vw;
  color: #596164;
}

/* 商家信息卡片 */
.business-card {
  background: white;
  margin: 12vw 10px 10px 10px;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.business-info {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.business-logo {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  margin-right: 12px;
}

.business-details h3 {
  font-size: 16px;
  font-weight: bold;
  margin: 0 0 8px 0;
  color: #333;
}

.business-address {
  font-size: 12px;
  color: #666;
  margin: 0;
}

.business-actions {
  display: flex;
  justify-content: flex-end;
}

.action-btn {
  background: #ffb900;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 12px;
  cursor: pointer;
}

/* 商品信息卡片 */
.product-card {
  background: white;
  margin: 0 10px 10px 10px;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
}

.product-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  margin-right: 12px;
  object-fit: cover;
}

.product-info h4 {
  font-size: 14px;
  font-weight: bold;
  margin: 0;
  color: #333;
}

/* 评论筛选标签 */
.comment-filter {
  display: flex;
  background: white;
  margin: 0 10px 10px 10px;
  border-radius: 8px;
  padding: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.filter-item {
  flex: 1;
  text-align: center;
  padding: 12px 0;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
}

.filter-item.active {
  color: #ffb900;
  border-bottom-color: #ffb900;
  font-weight: bold;
}

/* 评论列表 */
.comment-list {
  margin: 0 10px;
}

.comment-item {
  background: white;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 用户信息 */
.user-info {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
}

.user-details {
  flex: 1;
}

.username {
  font-size: 14px;
  font-weight: bold;
  margin: 0 0 4px 0;
  color: #333;
}

.comment-date {
  font-size: 12px;
  color: #999;
  margin: 0;
}

/* 评分 */
.rating {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.star {
  color: #ddd;
  font-size: 16px;
  margin-right: 2px;
}

.star.active {
  color: #ffb900;
}

.rating-text {
  margin-left: 8px;
  font-size: 14px;
  color: #ffb900;
  font-weight: bold;
}

/* 评论内容 */
.comment-content {
  margin-bottom: 10px;
}

.comment-content p {
  font-size: 14px;
  line-height: 1.5;
  color: #333;
  margin: 0;
}

/* 评论图片 */
.comment-images {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.comment-image {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  object-fit: cover;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 50px 20px;
  color: #999;
}

.empty-state p {
  font-size: 16px;
  margin: 0;
}
</style>