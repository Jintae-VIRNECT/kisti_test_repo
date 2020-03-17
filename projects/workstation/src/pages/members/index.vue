<template>
  <div class="page-members">
    <div v-for="member in members" :key="member.id">
      <p>{{ member }}</p>
    </div>
  </div>
</template>

<script>
import memberService from '@/services/member'

export default {
  components: {},
  data() {
    return {
      members: [],
    }
  },
  async beforeMount() {
    this.members = await memberService.getDefaultMemberList()
  },
}
</script>
