<template>
  <div class="dashboard-container" >
<!--    <div class="dashboard-text">name: {{ name }}</div>-->
<div>
  <el-image class="banner-img" :src="require('@/assets/dash.jpeg')">
  </el-image>

  <div class="banner-content">

    <svg-icon  icon-class="music" class="logo-img">
    </svg-icon>

    <h3 class="banner-title">一起探索无限世界</h3>
    <p class="banner-desc">音跃是多人在线音乐可视化3D创意社区，通过提供强大的编辑工具和素材，让用户能够尽情创作内容，并在虚拟社区中与伙伴一同体验交流、共同成长。</p>
      <el-button  type="primary" class="banner-button" @click="Addnew">
        创您所想
      </el-button>

<!--    <div class="banner-span">-->
<!--      <span >开始创造您的音乐可视化</span>-->
<!--    </div>-->


<!--    <span class="banner-desc">开始创造您的音乐可视化</span>-->
  </div>

</div>

  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'Dashboard',
  computed: {
    ...mapGetters([
      'name'
    ])
  },
  methods:{
    Addnew(){
      this.$router.push({
        name:'Editor'
      });
      this.$bus.$emit('addfile');


    }
  }
}
</script>

<style lang="scss">
.dashboard-container {
  //margin: 30px;
  background-color: #111111;

    .banner-img{
        width:100%;
      height: 100%;
    }
    .banner-content{
      text-align: center;
      position: absolute;
      top: 79%;
      left: 50%;
      transform: translate(-50%, -60%);
      -webkit-transform: translate(-50%, -60%);
      color: #ffffff;
      z-index: 1;
      width: 540px;
      .logo-img{
        width: 150px;
        height: 150px;
        margin-bottom: 18px;
        display: inline-block;
      }
      .banner-title{
        font-size: 32px;
        margin-bottom: 24px;
        font-weight: bold;
      }
      .banner-desc{
        font-size: 16px;
        color: #ffffff;
        width: 540px;
        margin: 0 auto;
      }
      .banner-button{
        margin-top: 10px;
      }

    }


}



</style>
