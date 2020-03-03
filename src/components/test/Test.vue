<template>
  <div class="test-wrapper">
    <header class="header">
      <h1 class="logo"><a href="/">Test Remote</a></h1>
    </header>
    <aside class="test-side">
      <nav class="test-nav">
        <router-link
          v-for="(component, index) in components"
          :key="index"
          :to="`/test/${component}`"
          tag="button"
        >
          {{ component }}
        </router-link>
      </nav>
    </aside>
    <main class="test-content">
      <div v-for="(component, index) in components" :key="index">
        <component v-if="tab === component" :is="component"></component>
      </div>
    </main>
  </div>
</template>
<script>
const Components = require('./')

export default {
  components: {
    ...Components,
  },
  computed: {
    components() {
      return Object.keys(Components)
    },
    tab() {
      return this.$route.params.tab
    },
  },

  /* Lifecycles */
  created() {
    if (this.components.indexOf(this.$route.params.tab) < 0) {
      this.$router.replace('/test/TestElementAdmin')
    }
  },
}
</script>
<style lang="scss">
@import '~assets/style/common';
@import './theme';

:root .dark {
  // background-color: #5a5a5a;
  @include dark-theme();
  // $bg-color: #5a5a5a !global;
}
:root .bright {
  // background-color: #fff;
  @include bright-theme();
  // $bg-color: #fff !global;
}

code {
  white-space: pre;
}
.test-wrapper {
  $header_height: 80px;
  position: relative;
  height: 100vh;
  padding-top: $header_height;

  overflow: hidden;
  @include clearfix;

  section {
    padding: 20px 30px 30px;
  }

  .header {
    position: absolute;
    top: 0;
    left: 0;
    z-index: 10;
    width: 100%;
    height: $header_height;
    padding: 0 30px;
    background-color: #3a3a3a;

    .logo {
      margin: 0;
      padding: 20px 0 22px;
      color: #fff;
      font-weight: 700;
      font-family: Arial;
    }
    .logo a {
      color: inherit;
      text-decoration: none;
    }
  }

  .test-side {
    float: left;
    width: 230px;
    height: 100%;
    background-color: #267bff;

    .test-nav {
      padding: 30px 0;
      button {
        display: block;
        width: 100%;
        padding: 12px 15px;
        color: #fff;
        font-size: 14px;
        text-align: left;
        background: none;
        border: none;

        &.active {
          font-weight: bold;
          text-decoration: underline;
          background-color: #0064ff;
        }
        &:hover {
          background-color: #0064ff;
        }
      }
    }
  }

  .test-content {
    position: relative;
    height: 100%;
    overflow-y: auto;
  }
}
</style>
