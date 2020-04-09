<template>
  <nav class="the-top-nav">
    <div class="the-top-nav__left">
      <nuxt-link :to="logo.path"
        ><img :src="logo.image" :alt="title"
      /></nuxt-link>
      <el-divider direction="vertical" />
      <h1>{{ title }}</h1>
    </div>
    <div class="the-top-nav__right">
      <div class="the-top-nav__profile">
        <span v-if="user" class="the-top-nav__profile__image">
          <img :src="user.image" />
        </span>
        <span v-if="user">{{ user.name }}</span>
        <button class="the-top-nav__profile__button" @click="loginBtnClick">
          {{ user ? '로그아웃' : '로그인' }}
        </button>
      </div>
    </div>
  </nav>
</template>

<script>
export default {
  props: {
    logo: Object,
    title: String,
    user: Object,
  },
  methods: {
    loginBtnClick() {
      this.$emit('loginBtnClick')
    },
  },
}
</script>

<style lang="scss">
$profile-image-size: 36px;

.the-top-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 30px;

  & > div > * {
    display: inline-block;
    vertical-align: middle;
  }
  & h1 {
    color: #0d2a58;
    font-size: 1.2rem;
  }
}
.the-top-nav__profile {
  & > * {
    display: inline-block;
    margin: 0 2px;
    vertical-align: middle;
  }
  &__image {
    width: $profile-image-size;
    height: $profile-image-size;
    overflow: hidden;
    background: #eaedf3;
    border-radius: 50%;
    & > img {
      width: 100%;
    }
  }
  &__button {
    margin-left: 20px;
    padding: 10px 20px;
    background-color: #eaedf3;
    border-radius: 3px;

    &:hover {
      background-color: #e6e9ee;
    }
    &:active {
      color: #fff;
      background-color: #1665d8;
    }
  }
}
</style>
