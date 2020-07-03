<template>
  <div class="workspace-license offsetwidth">
    <section class="workspace-license__description">
      <p class="workspace-license__description--title">
        VIRNECT Remote2.0을 사용할 수 있는 라이선스가 만료되었습니다.
      </p>
      <p class="workspace-license__description--sub-title">
        다음 라이선스 적용 전에 남은 기간 동안 Remote2.0을 사용하시려면 추가
        라이선스를 구매하여 이용해 주세요.
      </p>
    </section>
    <section class="workspace-license__cards">
      <workspace-license-card
        v-for="(data, idx) in licenseData"
        :key="idx"
        :plan="data.plan"
        :planName="data.planName"
        :perCount="data.perCount"
        :perPerice="data.perPerice"
        :condition="data.condition"
        :items="data.items"
      >
      </workspace-license-card>
    </section>
  </div>
</template>

<script>
import WorkspaceLicenseCard from '../partials/WorkspaceLicenseCard'
import licenseData from '../../../assets/json/license.json'
export default {
  components: {
    WorkspaceLicenseCard,
  },

  data() {
    return {
      licenseData: licenseData.licenseInfo,
    }
  },
  mounted() {
    console.log(licenseData)
  },
}
</script>

<style></style>
