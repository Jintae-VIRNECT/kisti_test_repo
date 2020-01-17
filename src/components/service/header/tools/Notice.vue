<template>
<popover
  trigger="click"
  placement="bottom-end">
  <toggle-button
    customClass="notice"
    slot="reference"
    description="알림"
    :size="34"
    :toggle="false"
    :activeSrc="require('assets/image/call/gnb_ic_notifi_nor.png')"
    @action="notice"
  ></toggle-button>

  <div>알림</div>
</popover>
</template>

<script>
import Popover from 'Popover'
import ToggleButton from 'ToggleButton'
export default {
	name: "Notice",
	components: {
    Popover,
    ToggleButton,
  },
	data() {
		return {}
  },
	watch: {
  },
	methods: {
    notice() {
    }
  },

	/* Lifecycles */
	mounted() {}
}
</script>