<template>
  <nav class="sidebar">
    <div class="sidebar-header">
      <img src="~assets/img/ic_setting.svg" /><span>메뉴</span>
    </div>
    <div class="sidebar-link" v-for="(link, index) in links" :key="index">
      <img :src="link.ic" /><router-link :to="link.path"
        ><span>{{ link.name }}</span></router-link
      >
    </div>
  </nav>
</template>

<script>
export default {
  name: 'SideBar',
  data() {
    return {
      links: [
        {
          name: '대시보드',
          path: '/',
          ic: require('@/assets/img/chat_ic_folder_w.svg'),
        },
        {
          name: '파일관리',
          path: '/filemanage',
          ic: require('@/assets/img/ic_chat_file_w.svg'),
        },
        {
          name: '녹화파일보기',
          path: '/recordfiles',
          ic: require('@/assets/img/ic_chat_video_w.svg'),
        },
      ],
    }
  },
}
</script>

<style lang="scss">
.sidebar {
  position: fixed;
  top: 78px;
  bottom: 0;
  left: 0;
  z-index: 4;
  width: 230px;

  max-height: 847px;
  margin-left: 20px;
  background: linear-gradient(0deg, #145fc6 0%, #0f75f5 100%);
  border-radius: 5px;
}

.sidebar-header {
  position: relative;
  display: flex;
  align-items: center;
  height: 62px;
  padding-left: 30px;
  font-weight: 400;
  font-size: 15px;

  > img {
    padding-right: 20px;
  }

  &::after {
    position: absolute;
    right: 15px;
    bottom: 0;
    width: calc(100% - 30px);
    height: 1px;
    background: rgba(#fff, 0.5);
    content: '';
  }
}

.sidebar-link {
  display: flex;
  align-items: center;
  height: 62px;
  padding-left: 30px;

  &:hover {
    font-weight: 500;
    cursor: pointer;
  }

  > img {
    width: 24px;
    height: 24px;
  }
  span {
    padding-left: 18px;
  }
}
</style>
