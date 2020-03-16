<template>
  <div class="page-members">
    <div v-for="member in members" :key="member.id">
      {{ member }}
    </div>
  </div>
</template>

<script>
import memberData from '@/data/member'

export default {
  components: {},
  data() {
    return {
      members: [],
    }
  },
  async beforeMount() {
    this.members = await memberData.getMemberList()
  },
}
</script>
