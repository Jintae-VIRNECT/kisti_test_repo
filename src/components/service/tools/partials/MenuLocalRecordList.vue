<template>
  <menu-button
    :text="$t('service.record_local_list')"
    :active="modalRecord"
    :src="require('assets/image/ic_local_record_list.svg')"
    @click="list"
  ></menu-button>
</template>

<script>
import toolMixin from './toolMixin'
import { mapGetters, mapActions } from 'vuex'
export default {
  name: 'LocalRecordListMenu',
  mixins: [toolMixin],
  computed: {
    ...mapGetters(['modalRecord']),
  },
  methods: {
    ...mapActions(['showModalRecord']),
    list() {
      this.showModalRecord(!this.modalRecord)
    },
  },
}
</script>
