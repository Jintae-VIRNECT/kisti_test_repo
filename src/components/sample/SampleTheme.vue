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
      document.querySelector('html').setAttribute('theme', theme)
    },
  },
}
</script>

<style lang="scss" scoped>
$dark-button: #000;
$dark-button-text: #fff;
$bright-button: #fff;
$bright-button-text: #000;
$button-color: $dark-button;
$button-text-color: $dark-button-text;
@mixin theme-dark() {
  @at-root [theme='dark'] & {
    @content;
  }
}
@mixin theme-bright() {
  @at-root [theme='bright'] & {
    @content;
  }
}

:root [theme='dark'] {
  $button-color: #{$dark-button} !global;
  $button-text-color: #{$dark-button-text} !global;
}

:root [theme='bright'] {
  $button-color: #{$bright-button} !global;
  $button-text-color: #{$bright-button-text} !global;
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
    color: $button-text-color;
    background-color: $button-color;
    border-radius: 4px;
    // @include theme-dark {
    //   background-color: #000;
    //   color: #fff;
    // }
    // @include theme-bright {
    //   background-color: #eee;
    //   color: #000;
    // }
  }
}
</style>
