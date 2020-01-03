<template lang="pug">
  div
    div(v-for="ud in usersData")
      member-profile-card(:profileData="ud")
</template>

<script>
import MemberProfileCard from '@/components/members/MemberProfileCard.vue'
import usersData from '@/data/users'

export default {
	components: {
		MemberProfileCard,
	},
	data() {
		return {
			usersData,
		}
	},
}
</script>
