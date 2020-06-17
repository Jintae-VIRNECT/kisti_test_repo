<template>
  <el-table :data="workspaces">
    <column-default
      :label="$t('workspace.list.column.name')"
      prop="name"
      sortable
    />
    <column-user
      :label="$t('workspace.list.column.master')"
      prop="masterNickName"
      nameProp="masterNickName"
      imageProp="masterProfile"
      :width="120"
      sortable
    />
    <column-date
      :label="$t('workspace.list.column.joinDate')"
      prop="joinDate"
      :width="100"
      sortable
    />
    <column-role
      :label="$t('workspace.list.column.role')"
      prop="role"
      :width="150"
      sortable
    />
  </el-table>
</template>

<script>
import columnMixin from '@/mixins/columns'

export default {
  mixins: [columnMixin],
  props: {
    workspaces: Array,
  },
}
</script>
