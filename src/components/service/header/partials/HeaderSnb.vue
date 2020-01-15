<template>
<div class="header-snb">
  <snb-button
    text="실시간 공유"
    keyvalue="stream"
    :image="require('assets/image/call/gnb_ic_shareframe.png')"
  ></snb-button>
  <snb-button
    text="일반 기능"
    keyvalue="drawing"
    :image="require('assets/image/call/gnb_ic_creat_basic.png')"
  ></snb-button>
  <snb-button
    text="AR 기능"
    keyvalue="ar"
    :image="require('assets/image/call/gnb_ic_creat_ar.png')"
  ></snb-button>
</div>
</template>

<script>
import SnbButton from './SnbButton'
export default {
	name: "HeaderSnb",
	components: {
    SnbButton
  },
	data() {
    return {}
	},

	/* Lifecycles */
	mounted() {
  }
}
</script>