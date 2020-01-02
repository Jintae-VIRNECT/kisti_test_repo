<template lang="pug">
  div
    div(v-for="ud in usersData")
      profile-card(:profileData="ud")
</template>

<script>
import profileCard from '@/components/common/ProfileCard.vue'
import usersData from '@/data/users'

export default {
	components: {
		profileCard,
	},
	data() {
		return {
			usersData,
		}
	},
	mounted() {
		console.log('usersData : ', this.usersData)
	},
}
</script>
