<template>
  <div class="about">
    <el-container>
      <el-header style="text-align: center; height: 100px">
        <el-button @click="Addnew" type="primary" style="margin-top: 20px" >
          创您所想
        </el-button>
       <div style="margin-top: 10px">
         <span>开始创造您的音乐可视化</span>
       </div>
      </el-header>
      <el-main>
        <el-row>
          <el-col
            v-for="(item,index) in fileData"
            :key="item.id"
            :span="5"
            style="margin-bottom: 10px"
            :offset="1"
          >
            <el-card style="height: 330px;width: 300px" >
              <div slot="header" class="clearfix">
                <i class="el-icon-user" /><span style="margin-left: 5px">{{
                  item.username
                }}</span>
                <div
                  style="display: inline-block; float: right; cursor: pointer"
                  @click="Edit(item.id,item.file)"
                >
                  <el-tooltip effect="dark" content="编辑文件" placement="top">
                    <i class="el-icon-edit-outline" style="margin-left: 15px" />
                  </el-tooltip>
                </div>
              </div>
              <div>
                <i class="el-icon-document" /><span style="margin-left: 5px">文件名：{{ item.filename }}</span>
<!--                <div class="role-left">文件名：{{ item.filename }}</div>-->
                <br><br>
                <el-image
                      style="width: 270px; height: 180px"
                      :src="'http://localhost:9090/img?id='+item.fileimg"
                      fit="content"></el-image>
              </div>
              <div
                style="display: inline-block; float: left; cursor: pointer"
                @click="Play(item.file)"
              >
                <el-tooltip effect="dark" content="场景展示" placement="bottom">
                  <i class="el-icon-menu" />
                </el-tooltip>
              </div>
              <div
                style="display: inline-block; float: right; cursor: pointer"
                @click="Delete(item.id,item.file,item.fileimg)"
              >
                <el-tooltip effect="dark" content="删除文件" placement="bottom">
                  <i class="el-icon-delete" style="margin-left: 15px" />
                </el-tooltip>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
      <el-footer>
        <div class="block">
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="pageNum"
            :page-sizes="[2, 5, 10, 20]"
            :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total">
          </el-pagination>
        </div>
      </el-footer>
    </el-container>


  </div>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
    name: 'Warehouse',
    watch:{
      $route:{
        handler(){
            this.GetAllByPages();
        },
        deep:true
      }
    },
  computed: {
    ...mapGetters([
      'name'
    ])
  },
    data(){
      return{
        fileData:[],
        total: 0,
        pageNum:1,
        pageSize: 2,
        userid:0,
        time:'',
        filename:'',

      }
    },
    methods:{

      Play(file){
        this.request.get("http://localhost:9090/getFileinfoByid",{
          params:{
            id:file
          }
        }).then(res=>{
          this.$router.push({
            name:'Scene'
          });

          this.$bus.$emit('playfile',res);
          //console.log(res);
        })
      },
      Edit(id,file){
        this.request.get("http://localhost:9090/getFileinfoByid",{
          params:{
            id:file
          }
        }).then(res=>{

          this.$router.push({
            name:'Editor'
          });
          //页面跳转
          this.$bus.$emit('eidtfile',res,id);
        })
      },
      Addnew(){
        this.$router.push({
          name:'Editor'
        });
        this.$bus.$emit('addfile');
      },
      Delete(id,file,fileimg){

        /*如果正在演示和编辑提示不能删除
        * */
        this.request.get("http://localhost:9090/deletebyid",{
          params:{
            id:file
          }
        }).then(res=>{
          console.log("删除图片"+res);
        });
        this.request.get("http://localhost:9090/deletebyid",{
          params:{
            id:fileimg
          }
        }).then(res=>{
          console.log("删除场景"+res);
        });
        this.request.delete("http://localhost:9090/scenefile/"+id).then(res=>{
          console.log("删除文件"+res);
        });
        this.pageNum=1;
        //每次删除后把页面数重置
        this.GetAllByPages();
      },
      Reset(){
        this.filename='';
        this.pageNum=1;
        //分页重置
        this.GetAllByPages();
      },
        GetAllByPages(){
          //请求分页查询数据
          let that=this;
          console.log(this.name)
          this.request.get("http://localhost:9090/user/getid", {
            params:{
              name:this.name
            }
          }).then(res=>{

            that.userid=res.id;
            console.log(res);
            that.request.get("http://localhost:9090/scenefile/page",{
              params:{
                pageNum:that.pageNum,
                pageSize:that.pageSize,
                userid:that.userid,
                filename:that.filename
              }
            }).then(res=>{
              that.fileData=res.records;
              that.total=res.total;
              console.log(res);
              // this.GetImg();
            })
          });

        },
      handleSizeChange(pageSize){
          this.pageSize=pageSize;
          this.GetAllByPages();
      },
      handleCurrentChange(pageNum){
          this.pageNum=pageNum;
          this.GetAllByPages();
      }
    },
    mounted() {
        this.GetAllByPages();
    }
}
</script>

<style>

</style>
