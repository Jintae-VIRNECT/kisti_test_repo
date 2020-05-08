<template>
  <div class="subview-wrapper">
    <!-- <sub-video></sub-video> -->
    <chat></chat>
  </div>
</template>

<script>
import SubVideo from './SubVideo'
import Chat from './Chat'
export default {
  name: 'SubViewLayout',
  components: {
    SubVideo,
    Chat,
  },
  data() {
    return {}
  },
  computed: {},
  watch: {},
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>
