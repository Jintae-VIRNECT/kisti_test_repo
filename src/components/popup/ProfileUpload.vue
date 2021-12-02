<template>
  <el-dialog
    :title="$t('user.profileImage.imageSet')"
    :visible.sync="isProfilePopup"
    width="400px"
    :before-close="uploadPopupClose"
    @open="uploadPopupOpen"
  >
    <div>
      <p class="contents">
        {{ $t('user.profileImage.visible') }}
      </p>
      <div class="image-holder pop-profile" @click="isProfilePopup = true">
        <input
          type="file"
          id="profileImage"
          accept=".png, .jpeg, .jpg"
          @change="uploadImage"
          ref="imgUpload"
        />
        <label for="profileImage" class="avatar">
          <div
            class="image"
            v-if="inputImg"
            :style="`background-image: url(${inputImg})`"
          ></div>
        </label>
      </div>
      <div class="el-upload__tip">
        {{ $t('user.profileImage.limit') }}
      </div>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button
        type="info"
        @click="imageUploadBtn"
        class="left-btn"
        :disabled="!thumbnailImageBtnDisabled"
        >{{ $t('user.profileImage.upload') }}</el-button
      >
      <el-button @click="deleteImage" :disabled="inputImg === null">{{
        $t('user.profileImage.delete')
      }}</el-button>
      <el-button
        type="primary"
        @click="thumbnailImageRegistBtn"
        :disabled="thumbnailImageBtnDisabled"
        >{{ $t('user.profileImage.submit') }}</el-button
      >
    </span>
  </el-dialog>
</template>

<script>
export default {
  props: {
    isProfilePopup: Boolean,
    inputImg: String,
    thumbnailImageBtnDisabled: Boolean,
  },
  setup(props, { emit }) {
    const uploadPopupOpen = () => {
      emit('uploadPopupOpen')
    }
    const uploadPopupClose = () => {
      emit('uploadPopupClose')
    }
    const uploadImage = e => {
      emit('uploadImage', e)
    }
    const imageUploadBtn = () => {
      emit('imageUploadBtn')
    }
    const deleteImage = () => {
      emit('deleteImage')
    }
    const thumbnailImageRegistBtn = () => {
      emit('thumbnailImageRegistBtn')
    }

    return {
      uploadPopupOpen,
      uploadPopupClose,
      uploadImage,
      imageUploadBtn,
      deleteImage,
      thumbnailImageRegistBtn,
    }
  },
}
</script>
