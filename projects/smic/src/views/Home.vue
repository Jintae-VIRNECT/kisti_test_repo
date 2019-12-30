<template lang="pug">
	div.text-center
		the-page-header(title="Home")
		h2 {{ $t("home.hello")}}
		.block
			span.demonstration Default
			el-date-picker(v-model='value1' type='datetime' placeholder='Select date and time')
			h1 {{ value1 }}
</template>

<script>
import ThePageHeader from '@/components/common/ThePageHeader.vue'

export default {
	components: { ThePageHeader },
	data() {
		return {
			value1: '',
		}
	},
}
</script>
