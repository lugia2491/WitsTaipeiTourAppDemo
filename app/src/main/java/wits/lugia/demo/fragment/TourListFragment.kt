package wits.lugia.demo.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableObserver
import org.json.JSONArray
import wits.lugia.demo.model.DataResponseModel
import wits.lugia.demo.viewModel.DataViewModel
import wits.lugia.demo.R
import wits.lugia.demo.activity.MainActivity
import wits.lugia.demo.adapter.AttractionsListAdapter
import wits.lugia.demo.data.AttractionItemData
import wits.lugia.demo.databinding.FragmentTourListBinding
import wits.lugia.demo.factory.ViewModelFactory

/**
 * 景點清單頁面
 */
class TourListFragment : Fragment() {
    //可null類型，離開時需清空
    private var _binding: FragmentTourListBinding? = null
    //方便使用
    private val binding get() = _binding!!
    private lateinit var mAttractionsListAdapter: AttractionsListAdapter
    private lateinit var mainActivity: MainActivity
    private lateinit var dataViewModel: DataViewModel
    private val cdAdapterItemClick = CompositeDisposable()
    private var nextPage = false//是否前往下一頁

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //與主畫面建立關係
        mainActivity = context as MainActivity
    }

    /** 初始化 */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //進場動畫
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_left)
        //出場
        exitTransition = inflater.inflateTransition(R.transition.slide_left)
    }

    /** 載入元件 */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentTourListBinding.inflate(inflater, container, false)
        //載入ViewModel
        dataViewModel = ViewModelProvider(this, ViewModelFactory{ DataViewModel(DataResponseModel()) })[(DataViewModel::class.java)]

        return binding.root
    }

    /** 初始化UI */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.srlTourList
            .setEnableLoadMore(true) //是否啟用拉到底載入功能
            .setEnableOverScrollDrag(true) //是否啟用越界拖動（仿蘋果效果）1.0.4
            .setDragRate(0.6f) //阻尼係數
            .setOnRefreshListener { refreshList() }
            .setOnLoadMoreListener { //lambda簡寫: 往下讀取
                updateList(true)
            }

        mAttractionsListAdapter = AttractionsListAdapter(JSONArray(), binding.root.context)
        //上方已帶入資料，不需要另外init
//        mAttractionsListAdapter.initDeviceUserManagerAdapter(binding.root.context, JSONArray())
        //決定清單樣式與方向
        binding.rvFmTourList.layoutManager = LinearLayoutManager(binding.root.context)
        //將Adapter套用至RecyclerView
        binding.rvFmTourList.adapter = mAttractionsListAdapter

        initObserve()

        updateList(false)
    }

    /** 初始化觀察者 */
    private fun initObserve(){
        //等待回應
        dataViewModel.getApiLiveData().observe(viewLifecycleOwner){data ->
            Log.d("資料已更新", data.toString())
            mAttractionsListAdapter.updateList(data.getJSONArray("data"))
            mainActivity.showLoading(false)
            binding.srlTourList.finishRefresh(true)
            if(nextPage){
                binding.srlTourList.finishLoadMore(true)
            }
        }
        dataViewModel.getErrorLiveData().observe(viewLifecycleOwner){
            mainActivity.showLoading(false)
            binding.srlTourList.finishRefresh(true)
            Toast.makeText(binding.root.context, "連線失敗", Toast.LENGTH_LONG).show()
        }

        //清單點擊
        cdAdapterItemClick.add(mAttractionsListAdapter.getItemClickObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<AttractionItemData>() {
                override fun onNext(mAttractionItemData: AttractionItemData) {
                    //前往詳細頁面
                    mainActivity.goDetailed(mAttractionItemData.itemData)
                }

                override fun onError(e: Throwable) {}

                override fun onComplete() {}
            }))
    }

    /**
     * 更新清單
     * @param addMode 是否往下一頁
     */
    private fun updateList(addMode: Boolean){
        this.nextPage = addMode
        mainActivity.showLoading(true)
        //開始讀取資料
        dataViewModel.callApi(addMode, mainActivity.getLanguage())
    }

    /**
     * 重整清單
     */
    private fun refreshList(){
        mAttractionsListAdapter.updateList(JSONArray())
        updateList(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //結束時釋放
        cdAdapterItemClick.dispose()
        _binding = null
    }
}