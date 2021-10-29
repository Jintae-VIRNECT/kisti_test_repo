<template>
  <nav class="header-lnbs service">
    <ul class="flex">
      <lnb-button
        v-for="menu of tabMenus"
        :key="menu.key"
        :text="$t(menu.text)"
        :active="view === menu.key"
        :image="menu.icon"
        :notice="menu.notice"
        @click="goTab(menu.key)"
      ></lnb-button>
    </ul>
  </nav>
</template>

<script>
import LnbButton from '../tools/LnbButton'
import tabChangeMixin from 'mixins/tabChange'

// import web_test from 'utils/testing'
export default {
  name: 'HeaderServiceLnb',
  mixins: [tabChangeMixin],
  components: {
    LnbButton,
  },
}
</script>
