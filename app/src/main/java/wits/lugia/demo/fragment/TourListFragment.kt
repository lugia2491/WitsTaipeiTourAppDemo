package wits.lugia.demo.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import org.json.JSONArray
import org.json.JSONObject
import wits.lugia.demo.factory.DataFactory
import wits.lugia.demo.model.DataRepository
import wits.lugia.demo.viewModel.DataViewModel
import wits.lugia.demo.R
import wits.lugia.demo.activity.MainActivity
import wits.lugia.demo.adapter.AttractionsListAdapter

/**
 * 景點清單頁面
 */
class TourListFragment : Fragment() {
    private lateinit var v: View
    private lateinit var rvList: RecyclerView
    private lateinit var srlTourList: SmartRefreshLayout
    private lateinit var cfFooter: ClassicsFooter
    private lateinit var mAttractionsListAdapter: AttractionsListAdapter

    private lateinit var mainActivity: MainActivity
    private lateinit var dataRepository: DataRepository
    private lateinit var dataFactory: DataFactory
    private lateinit var dataViewModel: DataViewModel

    private var pageLoad = 1//目前頁碼
    private var tmpData = JSONObject()//已下載的資料
    private var addMode = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //與主畫面建立關係
        mainActivity = context as MainActivity
    }

    //初始化
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //進場動畫
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_left)
        //出場
        exitTransition = inflater.inflateTransition(R.transition.slide_left)
    }

    //載入元件
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_tour_list, container, false)
        rvList = v.findViewById(R.id.rv_fm_tour_list)
        srlTourList = v.findViewById(R.id.srl_tour_list)
        cfFooter = v.findViewById(R.id.cf_tour_list_footer)

        dataRepository = DataRepository()
        dataFactory = DataFactory(dataRepository)
        dataViewModel = ViewModelProvider(this, dataFactory).get(DataViewModel::class.java)

        return v
    }

    //初始化UI
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        srlTourList.setEnableLoadMore(true) //是否啟用拉到底載入功能
        srlTourList.setEnableOverScrollDrag(true) //是否啟用越界拖動（仿蘋果效果）1.0.4
        srlTourList.setDragRate(0.6f) //阻尼係數
        srlTourList.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                refreshList()
            }
        })
        srlTourList.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore(refreshlayout: RefreshLayout) {
                //往下讀取
                pageLoad++
                updateList(pageLoad, true)
            }
        })

        //先將Adapter new出來
        mAttractionsListAdapter = AttractionsListAdapter()
        //初始化Adapter
        mAttractionsListAdapter.initDeviceUserManagerAdapter(v.context, JSONArray())
        //決定清單樣式與方向(這邊預設)
        rvList.layoutManager = LinearLayoutManager(v.context)
        //將Adapter套用至RecyclerView
        rvList.adapter = mAttractionsListAdapter
        //點擊監聽
        mAttractionsListAdapter.setAttractionsListClickListener(object :
            AttractionsListAdapter.AttractionsListClickListener {
            override fun viewClickListener(view: View?, clickData: JSONObject, position: Int) {
                //前往詳細頁面
                mainActivity.goDetailed(clickData)
            }
        })

        initObserve()

        updateList(pageLoad, false)
    }

    //初始化觀察者
    private fun initObserve(){
        //等待回應
        dataViewModel.getApiLiveData().observe(viewLifecycleOwner){data ->
            Log.d("更新", data.toString())
            tmpData = data
            mAttractionsListAdapter.updateList(data.getJSONArray("data"))
            mainActivity.showLoading(false)
            srlTourList.finishRefresh(true)
            if(addMode){
                srlTourList.finishLoadMore(true)
            }
        }
    }

    /**
     * 更新清單
     * @param page 頁碼，1開始
     */
    private fun updateList(page: Int, addMode: Boolean){
        this.addMode = addMode
        mainActivity.showLoading(true)
        //開始讀取資料
        //是否往後增加資料
        if(addMode){
            dataViewModel.callApi(page.toString(), tmpData, mainActivity.getLanguage())
        }else{
            dataViewModel.callApi(page.toString(), null, mainActivity.getLanguage())
        }

    }

    /**
     * 重整清單
     */
    fun refreshList(){
        pageLoad = 1
        mAttractionsListAdapter.updateList(JSONArray())
        updateList(pageLoad, false)
    }
}