<template>
  <div style="padding:30px;">
    <el-alert :closable="false" title="menu 1">
      <router-view />
    </el-alert>
  </div>
</template>
<script>
export default {
  name:'Menu1'
}
</script>
