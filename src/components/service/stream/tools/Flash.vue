<template>
  <tool-button
    :text="$t('service.tool_flash')"
    :disable="disable"
    :isActive="status"
    :src="require('assets/image/ic_flash_off.svg')"
    :activeSrc="require('assets/image/ic_flash_on.svg')"
    placement="top"
    @click="clickHandler()"
  ></tool-button>
</template>

<script>
import flashMixin from 'mixins/flash'
import ToolButton from 'ToolButton'

export default {
  name: 'ToolFlash',
  components: {
    ToolButton,
  },
  mixins: [flashMixin],
}
</script>
