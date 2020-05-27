<template>
  <div class="test">
    <h1 class="test-title">테스트</h1>
    <section class="test-section" style="background-color: #1e1e20;">
      <h2 class="subtitle">Modalless</h2>
      <div class="action-box">
        <div class="component">
          <button @click="modalTest = true" style="color: #fff;">
            Show Modal Less pop up
          </button>
          <device-denied :visible.sync="modalTest"></device-denied>
        </div>
      </div>
    </section>
  </div>
</template>
<script>
import DeviceDenied from 'components/workspace/modal/WorkspaceDeviceDenied'
export default {
  data: function() {
    return {
      modalTest: false,
    }
  },
  components: {
    DeviceDenied,
  },
}
</script>

<style lang="scss" scoped>
.test {
  height: 100%;
  padding: 30px;
}

.text-title {
  margin-bottom: 30px;
  font-weight: 500;
}

.test-section {
  margin-bottom: 30px;
  background-color: #787878;
  border-radius: 10px;

  .subtitle {
    color: #fff;
    font-weight: 500;
  }
  .action-box {
    display: flex;
    .component {
      position: relative;
      width: 100%;
    }

    .props {
      flex-shrink: 0;
      width: 600px;
      &-option {
        display: flex;
        margin-bottom: 10px;
      }
      &-title {
        flex-shrink: 0;
        width: 100px;
        padding-right: 10px;
        color: #fff;
        text-align: right;
      }
      &-options {
        width: 100%;
      }
    }
  }
}
</style>
