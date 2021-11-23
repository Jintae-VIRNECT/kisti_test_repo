<template>
  <div class="share">
    <ul class="share-title">
      <li>
        <button class="share-title__button ar-3d-contents">
          {{ $t('service.file_list') }}
        </button>
      </li>
    </ul>
    <div class="share-body">
      <file-list ref="shareFileList"></file-list>
    </div>
  </div>
</template>

<script>
import FileList from './ShareFileList'

export default {
  name: 'Share3D',
  components: {
    FileList,
  },
}
</script>

<style lang="scss">
.share-title__button.ar-3d-contents {
  background-color: transparent;
  margin-left: 1.928rem;
  width: unset;
}
</style>
