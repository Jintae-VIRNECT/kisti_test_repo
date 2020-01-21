<template>
<div class="share-list">
  <div class="share-list__title">
    <p>공유 목록</p>
  </div>
  <div class="share-list__body">
    <vue2-scrollbar>
    <ol class="upload-list">
      <li><button class="upload-list__button">파일추가</button></li>
      <sharing-image
        v-for="(image, idx) of sharingList"
        :key="'sharing'+idx"
      ></sharing-image>
    </ol>
    </vue2-scrollbar>
  </div>
  <button class="share-list__save">저장하기</button>
</div>
</template>

<script>
import SharingImage from './partials/SharingImage'
export default {
	name: "ShareList",
	components: {
    SharingImage
  },
	data() {
		return {
      sharingList: [{
        title: '화면설계.png'
      },{
        title: '화면설계.png'
      }]
    }
	},
	computed: {},
	watch: {},
	methods: {},

	/* Lifecycles */
	mounted() {}
}
</script>
