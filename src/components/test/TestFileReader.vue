<template>
  <div class="test">
    <h1 class="test-title">File Reader</h1>

    <section class="test-section">
      <h2 class="subtitle">Image</h2>
      <div class="action-box">
        <div class="component"></div>
        <div class="props"></div>
      </div>
    </section>
    <section class="test-section">
      <h2 class="subtitle">Pdf</h2>
      <div class="action-box">
        <div class="component"></div>
        <div class="props">
          <div class="props-option">
            <p class="props-title">content</p>
            <input class="props-options" v-model="tooltip.content" />
          </div>
        </div>
      </div>
    </section>
    <section class="test-section">
      <h2 class="subtitle">Power Point</h2>
      <div class="action-box">
        <div class="component"></div>
        <div class="props"></div>
      </div>
    </section>
  </div>
</template>
<script>
// import
export default {
  components: {},
  data() {
    return {}
  },
  methods: {},
  mounted() {},
}
</script>

<style lang="scss" scoped>
.test {
  height: 100%;
  padding: 30px;

  &-title {
    margin-bottom: 30px;
    font-weight: 500;
  }

  &-section {
    margin-bottom: 30px;
    background-color: #bebebe;
    border-radius: 10px;
    .subtitle {
      color: #000;
      font-weight: 500;
    }
    .action-box {
      display: flex;
      .component {
        width: 100%;
      }

      .props {
        flex-shrink: 0;
        width: 250px;
        &-option {
          display: flex;
          margin-bottom: 10px;
        }
        &-title {
          flex-shrink: 0;
          width: 100px;
          padding-right: 10px;
          text-align: right;
        }
        &-options {
          width: 100%;
        }
      }
    }
  }
}
</style>
