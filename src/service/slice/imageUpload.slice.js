import { ref, computed } from '@vue/composition-api'
import { alertMessage } from 'mixins/alert'
import { validImage } from 'mixins/validate'

export default function imageUpload(
  root,
  thumbnail,
  formData,
  isProfilePopup,
  user,
) {
  const imgUpload = ref()
  const inputImg = ref(null)
  const file = ref(null)

  const thumbnailImageBtnDisabled = computed(() => {
    return thumbnail.value === inputImg.value
  })

  const uploadPopupOpen = () => {
    inputImg.value = thumbnail.value
  }
  const uploadPopupClose = () => {
    if (thumbnail.value === null) {
      file.value = null
    }
    // close()
    isProfilePopup.value = false
  }
  const uploadImage = async event => {
    const files = event.target.files
    formData.value.delete('profile') // profile는 컨텐츠 내의 이미지 리소스
    try {
      const imageData = await validImage(event, file)
      file.value = files[files.length - 1]
      inputImg.value = imageData
    } catch (e) {
      console.error(e)
      alertMessage(root, root.$t('user.error.etc.title'), e, 'error')
    }
    imgUpload.value.$refs.imgUpload.value = ''
  }
  const imageUploadBtn = () => {
    imgUpload.value.$refs.imgUpload.click()
    imgUpload.value.$refs.imgUpload.value = ''
  }
  const thumbnailImageRegistBtn = () => {
    isProfilePopup.value = false
    user.value.profile = file.value
    thumbnail.value = inputImg.value
  }
  const deleteImage = () => {
    file.value = null
    inputImg.value = null
  }
  return {
    imgUpload,
    inputImg,
    thumbnailImageBtnDisabled,
    uploadPopupOpen,
    uploadPopupClose,
    uploadImage,
    imageUploadBtn,
    thumbnailImageRegistBtn,
    deleteImage,
  }
}
