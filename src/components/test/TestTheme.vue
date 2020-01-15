<template>
  <div class="theme">
    <h1 class="theme-title">Theme Test</h1>

    <section>
      <h2 class="theme-subtitle">Theme change</h2>
      <div class="action-sandbox">
        <button class="theme-button" @click="setTheme('dark')">
          dark
        </button>
        <button class="theme-button" @click="setTheme('bright')">
          bright
        </button>
      </div>
    </section>
  </div>
</template>
<script>
export default {
  methods: {
    setTheme(theme) {
      document.querySelector("html").setAttribute("theme", theme);
    }
  }
};
</script>

<style lang="scss" scoped>
@mixin theme-dark() {
  @at-root [theme="dark"] & {
    @content;
  }
}
@mixin theme-bright() {
  @at-root [theme="bright"] & {
    @content;
  }
}

.theme {
  height: 100%;
  @include theme-dark {
    background-color: #5a5a5a;
    &-title {
      color: #fff;
    }
    &-subtitle {
      color: #fff;
    }
  }
  @include theme-bright {
    background-color: #fff;
    &-title {
      color: #000;
    }
    &-subtitle {
      color: #000;
    }
  }

  &-button {
    padding: 10px;
    border-radius: 4px;
    @include theme-dark {
      background-color: #000;
      color: #fff;
    }
    @include theme-bright {
      background-color: #eee;
      color: #000;
    }
  }
}
</style>
