<template>
  <el-card class="main-banner" v-if="!$onpremiseTarget">
    <h6>{{ $t('home.banner.sub') }}</h6>
    <h5>{{ $t('home.banner.main', { company }) }}</h5>
    <p>
      {{ $t('home.banner.desc_op', { company }) }}
    </p>
  </el-card>
  <!-- posco -->
  <el-card
    v-else-if="$onpremiseTarget === 'posco'"
    class="main-banner onpremise-banner posco"
  >
    <p>
      <span v-if="$i18n.locale === 'ko'">더불어 함께 발전하는 기업시민</span>
      <span v-else>
        Corporate Citizenship : Building a Better Future Together
      </span>
      <img src="~assets/images/onpremise/posco.png" />
    </p>
    <h6>Connect Work, Space and People</h6>
    <h5>Workstation for Posco - RIST Remote Collaboration Solution</h5>
  </el-card>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  computed: {
    ...mapGetters({
      company: 'layout/title',
    }),
  },
}
</script>

<style lang="scss">
#home .container .onpremise-banner {
  &.posco {
    p {
      margin-top: 0;
      margin-bottom: 20px;

      span,
      img {
        display: inline-block;
        margin-right: 8px;
        vertical-align: middle;
      }
      img {
        margin-bottom: 4px;
      }
    }
  }
}
</style>
