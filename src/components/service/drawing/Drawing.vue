<template>
  <div class="drawing-box">
    <drawing-canvas
      v-if="shareFile && shareFile.id"
      :file="shareFile"
    ></drawing-canvas>
    <div class="drawing-box__empty" v-else>
      <div class="drawing-box__empty-inner">
        <img src="~assets/image/call/img_fileshare.svg" />
        <p>
          파일을 더블클릭하면 <br />
          상대방에게 공유를 시작합니다
        </p>
      </div>
    </div>
  </div>
</template>

<script>
import DrawingCanvas from './DrawingCanvas'
import { mapGetters } from 'vuex'

export default {
  name: 'Drawing',
  components: {
    DrawingCanvas,
  },
  data() {
    return {}
  },
  computed: {
    ...mapGetters(['shareFile']),
  },
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>
