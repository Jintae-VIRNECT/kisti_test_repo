<template>
  <el-dialog
    :title="$t('user.profileImage.imageSet')"
    :visible.sync="profilePopup"
    width="400px"
    :before-close="uploadPopupClose"
    @open="uploadPopupOpen"
  >
    <div>
      <p class="contents">
        {{ $t('user.profileImage.visible') }}
      </p>
      <div class="image-holder pop-profile" @click="profilePopup = true">
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
    profilePopup: Boolean,
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

<style lang="scss" scoped>
// .el-dialog {
//   border: solid 1px #e6e9ee;
//   border-radius: 4px;
//   box-shadow: 0 2px 8px 0 rgba(8, 23, 48, 0.06),
//     0 2px 4px 0 rgba(8, 23, 48, 0.1);

//   .image-holder {
//     float: none;
//     width: 160px;
//     height: 160px;
//     margin: 44px auto 16px;
//     img {
//       width: 100%;
//       height: 100%;
//       -webkit-mask: url('~assets/images/common/ic-bg.svg') no-repeat;
//       -webkit-mask-size: 100%;
//       mask: url('~assets/images/common/ic-bg.svg') no-repeat;
//       mask-size: 100%;
//     }

//     @media (max-width: $mobile) {
//       width: 30vw;
//       height: 30vw;
//       margin: 16px auto;
//     }
//   }
//   &__wrapper {
//     text-align: left;
//   }
//   input[type='file'] {
//     position: absolute;
//     top: 0;
//     left: -9999px;
//   }
//   label {
//     position: absolute;
//     top: 0;
//     left: 0;
//     width: 100%;
//     height: 100%;
//     cursor: pointer;
//   }
//   .el-button {
//     height: 36px;
//     font-weight: normal;
//     font-size: 13px;
//     &.left-btn {
//       float: left;
//       @media (max-width: $mobile) {
//         display: block;
//         float: none;
//         margin: 0 auto 24px;
//       }
//     }
//   }
// }
// .el-button.next-btn {
//   margin-top: 60px;
// }
// .el-upload__tip {
//   color: #566173;
//   font-size: 13px;
//   text-align: center;
// }
</style>
