<template>
  <div class="Footer">
    <el-button @click="Save" type="primary" style="margin-left: 16px;">
        保存
    </el-button>
    <el-button @click="Reset" type="primary" style="margin-left: 16px;" >
        重置
    </el-button>
    <el-button @click="Export" type="primary" style="margin-left: 16px;">
        导出
    </el-button>

    <el-drawer
    title="文件"
    :visible.sync="drawer"
    :with-header="false"
    :before-close="handleClose"
    custom-class="demo-drawer"
    >
      <div class="demo-drawer__content">
        <el-form :model="form" label-width="80px">
          <el-form-item label="文件名">
            <el-input v-model="form.name" autocomplete="off"></el-input>
          </el-form-item>

          <el-form-item label="封面" label-width="80px">
            <el-upload
              class="upload-demo"
              drag
              action="#"
              :multiple="false"
              :show-file-list="true"
              :http-request="uploadHttpRequest"
              :file-list="fileList"
              :on-change="handleUploadChange"
              :before-upload="beforeUpload"
              >
              <i class="el-icon-upload"></i>
              <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
              <div class="el-upload__tip" slot="tip">只能上传jpg/png文件，且不超过500kb</div>
            </el-upload>
          </el-form-item>
          <el-form-item>
              <el-button type="primary" @click="onSubmit" :loading="loading">{{ loading ? '提交中 ...' : '立即保存' }}</el-button>
              <el-button @click="cancel">取消</el-button>
          </el-form-item>
        </el-form>
<!--        <div class="demo-drawer__footer">-->
<!--          <el-button @click="cancelForm">取 消</el-button>-->
<!--          <el-button type="primary" @click="$refs.drawer.closeDrawer()" :loading="loading">{{ loading ? '提交中 ...' : '确 定' }}</el-button>-->
<!--        </div>-->

      </div>
    </el-drawer>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
export default {

  /*bug:
  * 场景树在每次点击编辑后会改变 √
  * 跳转路由问题 √
  * 新增按钮 √
  * 图片异常问题 √
  * 播放器样式
  * 按钮样式
  * 提交抽屉优化 √
  * 加载动画
  * 关闭演示时音乐关闭
  * 登录*/
    data() {
        return {
            drawer:false,
            fileList:[],
          loading:false,
          id:null,
         file:null,
            form:{
              name:'',
              date1:'',
              date2:'',
              img:'',
            },
          userid:null,
          username:null,
          imgid:''

        }
    },
  computed: {
    ...mapGetters([
      'name'
    ])
  },
    methods: {
      Save(){
        this.drawer = true;
        if(this.id!=null){
          this.request.get("http://localhost:9090/scenefile/getbyid",{
            params:{
              id:this.id
            }
          }).then(res=>{

            this.form.name=res.file.filename;
            this.userid=res.file.userid;
            this.username=res.file.username;
          })
        }
      },
      onSubmit(){
        this.$bus.$emit('requestsave');
        this.$bus.$on('responsesave',(id)=>{

            let scenefile={
              id:this.id,
              userid:this.userid,
              username:this.name,
              filename:this.form.name,
              file: id,
              fileimg: this.imgid,

            }
            console.log(this.imgid);
            this.request.post("http://localhost:9090/scenefile",scenefile)
          .then(res=>{
            // console.log(res);
            this.loading=true;
            this.drawer=false;
            this.$message.success("保存成功！");
            this.form={

              name:'',
              date1:'',
              date2:'',
              img:''

            };


          })
        })
      },
      // Load(){
      // //console.log(this.file);
      // let param=new FormData();
      // param.append('file',this.file);
      // let config={headers:{'Content-Type':'multipart/form-data'}}
      //   this.request.post("http://localhost:9090/upload",
      //    param,config).then(res=>{
      //     //console.log(res);
      //     this.id=res.fileid;
      //   })
      //
      // },
      // Upload(e){
      //   this.file=e.target.files[0];
      // },
        Export(){
            this.$bus.$emit('export');
        },
        Reset(){
            this.$bus.$emit('reset');
          // this.request.get("http://localhost:9090/getFileinfoByid",{
          //   params:{
          //     id:this.id
          //   }
          // }).then(res=>{
          //   this.$bus.$emit('sendres',res);
          // })
        },
      beforeUpload(file) {
        const fileType = file.name.substring(file.name.lastIndexOf('.'))
        if (fileType.toLowerCase() != '.jpg' && fileType.toLowerCase() != '.png') {
          this.$message.error('文件必须为.jpg或.png类型')
          this.fileList = []
          return false
        }
      },
      // 覆盖element的默认上传文件
      uploadHttpRequest(data) {
        let reader = new FileReader()
        let that = this
        reader.readAsText(data.file)
        reader.onload = function() {
          that.form.img = this.result;
        }
        let param=new FormData();
        param.append('file',data.file);
        let config={headers:{'Content-Type':'multipart/form-data'}}
        this.request.post("http://localhost:9090/upload",
          param,config).then(res=>{
          console.log(res.fileid);
          this.imgid=res.fileid;
        })
      },
      // 限制文件上传的个数只有一个，获取上传列表的最后一个
      handleUploadChange(file, fileList) {
        if (fileList.length > 0) {
          this.fileList = [fileList[fileList.length - 1]] // 这一步，是 展示最后一次选择的文件
        }
      },
      getfileid(){
        this.$bus.$on('sendfileid',(id)=>{
          this.id=id;
        })
      },
      cancel(){
        this.loading=false;
          this.form={

              name:'',
              date1:'',
              date2:'',
              img:''

          }
      },
        handleClose(done) {
        this.$confirm('确认关闭？')
          .then(_ => {
            done();
          })
          .catch(_ => {});
      },
      getuserid(){
        this.request.get("http://localhost:9090/user/getid", {
          params:{
            name:this.name
          }
        }).then(res=>{
          this.userid=res.id;
        });
      }
    },

  mounted() {
      this.getuserid();
      this.getfileid();
  }
}
</script>

<style>

</style>
