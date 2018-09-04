package io.github.laelluo.ifwmanager

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView

/**
 * A simple [Fragment] subclass.
 * Use the [ComponentFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
//TODO(item多选)

class ComponentFragment : Fragment() {
    private lateinit var componentType: String
    private var components: Array<String> = arrayOf()
    private val banList = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            componentType = it.getString("ComponentType")
            components = it.getStringArray("Components")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val inflate = inflater.inflate(R.layout.fragment_component, container, false)
        components.sortBy { it.split(".").last() }
        inflate.findViewById<RecyclerView>(R.id.components_recycler_manager).apply {
            adapter = ComponentAdapter()
            layoutManager = LinearLayoutManager(context)
        }
        return inflate
    }

    companion object {
        @JvmStatic
        fun newInstance(components: Array<String>, componentType: String) =
                ComponentFragment().apply {
                    arguments = Bundle().apply {
                        putStringArray("Components", components)
                        putString("ComponentType", componentType)
                    }
                }
    }

    fun getBanList(): List<String> {
        return banList
    }

    inner class ComponentAdapter : RecyclerView.Adapter<ComponentAdapter.Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_component, parent, false))
        override fun getItemCount() = components.size
        override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(components[position])
        inner class Holder(val view: View) : RecyclerView.ViewHolder(view) {
            fun bind(name: String) {
//                TODO(初始化item)
                view.findViewById<TextView>(R.id.simple_name_text_component).text = name.split(".").last()
                view.findViewById<TextView>(R.id.name_text_component).text = name
                val switch = view.findViewById<Switch>(R.id.switch_item_component)
                switch.setOnCheckedChangeListener { _, b ->
                    if (b){
                        log("ban $name")
                        banList.add(name)
                    }else{
                        banList.remove(name)
                    }
                }
                view.setOnClickListener {
                    switch.isChecked = !switch.isChecked
                }
                view.setOnLongClickListener {
                    activity
                    true
                }
            }
        }
    }
}
