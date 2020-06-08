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
    <section class="test-section select" style="background-color: #1e1e20;">
      <h2 class="subtitle">Modal</h2>
      <div class="action-box">
        <div class="component">
          <button @click="modalTest2 = true" style="color: #fff;">
            레코드 테스트
          </button>
          <local-record-list :visible.sync="modalTest2"></local-record-list>
        </div>
      </div>
    </section>
    <section class="test-section select" style="background-color: #1e1e20;">
      <h2 class="subtitle">테이블</h2>
      <div class="action-box">
        <div class="component">
          <remote-table></remote-table>
        </div>
      </div>
    </section>
  </div>
</template>
<script>
import DeviceDenied from 'components/workspace/modal/WorkspaceDeviceDenied'
import LocalRecordList from 'components/workspace/modal/LocalRecordList'
import RemoteTable from 'RemoteTable'
export default {
  data: function() {
    return {
      modalTest: false,
      modalTest2: false,
    }
  },
  components: {
    DeviceDenied,
    LocalRecordList,
    RemoteTable,
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
