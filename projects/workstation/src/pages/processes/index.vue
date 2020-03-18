<template>
  <div id="processes">
    <div v-for="process in processes" :key="process.id">
      <p>{{ process }}</p>
    </div>
  </div>
</template>

<script>
import processService from '@/services/process'

export default {
  async asyncData() {
    return {
      processes: await processService.getDefaultProcessList(),
    }
  },
}
</script>
