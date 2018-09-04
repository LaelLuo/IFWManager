package io.github.laelluo.ifwmanager

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Rect
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.*
import android.widget.*
import de.hdodenhof.circleimageview.CircleImageView
import io.github.laelluo.ifwmanager.bean.AppBean
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val appAdapter = Adapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        初始化recycler swiperefresh
        apps_recycler_main.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
                    outRect?.set(0, 0, 0, 1)
                }
            })
            adapter = appAdapter
        }
        refresh_swiperefresh_main.apply {
            setOnRefreshListener { refreshApps() }
            setColorSchemeColors(0x3F51B5)
        }
        refreshApps()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
//        初始化SearchView点击事件
        menu?.findItem(R.id.search_item_menu_main)?.apply {
            //            搜索触发事件
            setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                    refresh_swiperefresh_main.isEnabled = false
                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                    refresh_swiperefresh_main.isEnabled = true
                    refreshApps()
                    return true
                }

            })
//            输入触发事件
            actionView?.let { it as SearchView }?.apply {
                setIconifiedByDefault(true)
                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?) = true

                    override fun onQueryTextChange(newText: String?): Boolean {
                        newText?.apply { appAdapter.filter.filter(newText) }
                        return true
                    }
                })
            }
        }
        menu?.findItem(R.id.switch_item_menu_main)?.actionView?.let { it as RelativeLayout }?.getChildAt(0).apply {
            this as Switch
            isChecked = Data.isOpen
            setOnCheckedChangeListener { _, b ->
                isChecked = b
                Data.isOpen = b
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.more_item_menu_main -> {
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun refreshApps() {
        refresh_swiperefresh_main.isRefreshing = true
//        获取App列表
        Thread {
            Data.apps.clear()
            appAdapter.list.clear()
            packageManager.getInstalledPackages(0).filter { it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0 }.map {
                AppBean(
                        it.applicationInfo.loadLabel(packageManager).toString(),
                        it.packageName,
                        it.versionName,
                        it.applicationInfo.loadIcon(packageManager),
                        packageManager.getPackageInfo(it.packageName, PackageManager.GET_SERVICES).services
                                ?.map { service -> service.name }
                                ?: listOf(),
                        packageManager.getPackageInfo(it.packageName, PackageManager.GET_RECEIVERS).receivers
                                ?.map { receiver -> receiver.name }
                                ?: listOf(),
                        packageManager.getPackageInfo(it.packageName, PackageManager.GET_ACTIVITIES).activities
                                ?.map { activity -> activity.name }
                                ?: listOf(),
                        packageManager.getPackageInfo(it.packageName, PackageManager.GET_PROVIDERS).providers
                                ?.map { provider -> provider.name }
                                ?: listOf()
                )
            }.sortedBy { it.label }.forEach {
                Data.apps.add(it)
                appAdapter.list.add(it)
            }
            runOnUiThread {
                appAdapter.notifyDataSetChanged()
                refresh_swiperefresh_main.isRefreshing = false
            }

        }.start()
    }

    inner class Adapter : RecyclerView.Adapter<Adapter.Holder>(), Filterable {
        val list = ArrayList<AppBean>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false))
        override fun getItemCount() = list.size
        override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(list[position])
        override fun getFilter() = object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                list.clear()
                val string = constraint?.toString()!!
                if (string.isEmpty()) {
                    list.addAll(Data.apps)
                } else {
                    Data.apps.forEach {
                        if (it.label.contains(string)) list.add(it)
                    }
                }
                return FilterResults()
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                notifyDataSetChanged()
            }
        }

        inner class Holder(val view: View) : RecyclerView.ViewHolder(view) {
            fun bind(app: AppBean) {
                view.findViewById<CircleImageView>(R.id.icon_image_app).setImageDrawable(app.icon)
                view.findViewById<TextView>(R.id.name_text_app).text = app.label
                view.setOnClickListener {
                    if (Data.apps.indexOf(app) != -1) {
                        startActivity(Intent(this@MainActivity, ManagerActivity::class.java).apply {
                            putExtra("position", Data.apps.indexOf(app))
                        })
                    }
                }
            }
        }
    }
}
