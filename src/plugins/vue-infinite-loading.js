import Vue from 'vue'
import InfiniteLoading from 'vue-infinite-loading'

Vue.use(InfiniteLoading, {
  slots: { noMore: '', noResults: '' },
  props: { spinner: 'bubbles' },
  system: { throttleLimit: 50 },
})
