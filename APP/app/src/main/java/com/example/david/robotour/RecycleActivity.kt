package com.example.david.robotour

import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView

class RecycleActivity : AppCompatActivity() {

    val data = ArrayList<String>()
    val KEY_LIST = "List"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let {
            val arrayList = savedInstanceState.get(KEY_LIST)
            data.addAll(arrayList as List<String>)
        }
        RecycleUI(Adapter(data)).setContentView(this)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putStringArrayList(KEY_LIST, data)
        super.onSaveInstanceState(outState)
    }
}

class RecycleUI(val listAdapter: Adapter) : AnkoComponent<RecycleActivity> {
    override fun createView(ui: AnkoContext<RecycleActivity>): View = with(ui) {
        return relativeLayout {
            horizontalScrollView {
                relativeLayout {
                    val emptyView = textView("Say something outrageous.") {
                        textSize = 16f
                        typeface = Typeface.MONOSPACE
                    }.lparams {
                        centerInParent()
                    }

                    // BUTTON
                    val pushPopButton = button("Push") {
                        //id = 2;
                    }.lparams {
                        topMargin = dip(8)
                        alignParentBottom()
                        alignParentRight()
                    }

                    // TEXT FIELD
                    val inputEditText = editText {
                        //id = 1;
                        hint = "Enter something"
                    }.lparams(width = matchParent) {
                        topMargin = dip(8)
                        sameBottom(pushPopButton)
                        leftOf(pushPopButton)
                    }

                    fun updateEmptyViewVisibility(recyclerView: RecyclerView) {
                        if (doesListHaveItem(recyclerView)) {
                            emptyView.visibility = View.GONE
                        } else {
                            emptyView.visibility = View.VISIBLE
                        }
                    }

                    // LIST
                    val list = recyclerView {
                        val orientation = LinearLayoutManager.HORIZONTAL
                        layoutManager = LinearLayoutManager(context, orientation, false)
                        //overScrollMode = View.OVER_SCROLL_NEVER
                        adapter = listAdapter
                        adapter.registerAdapterDataObserver(
                                object : RecyclerView.AdapterDataObserver() {
                                    override fun onItemRangeInserted(start: Int, count: Int) {
                                        updateEmptyViewVisibility(this@recyclerView)
                                    }

                                    override fun onItemRangeRemoved(start: Int, count: Int) {
                                        updateEmptyViewVisibility(this@recyclerView)
                                    }
                                })

                        updateEmptyViewVisibility(this)
                    }.lparams(height = wrapContent) {
                        above(inputEditText)
                    }

                    // BUTTON CLICK LISTENER
                    pushPopButton.onClick {
                        val inputText = inputEditText.text.toString()
                        val adapter = list?.adapter as Adapter

                        if (inputText.isBlank() &&
                                doesListHaveItem(list)) {
                            adapter.pop()
                        } else {
                            if (!inputText.isBlank()) {
                                adapter.push(inputText)
                                list.scrollToPosition(0)
                            }
                        }

                        inputEditText.setText("")
                    }

                    // TEXT FIELD TEXT CHANGED LISTENER
                    inputEditText.textChangedListener {
                        afterTextChanged {
                            if (it?.isBlank() ?: true &&
                                    doesListHaveItem(list)) {
                                pushPopButton.text = "Pop"
                            } else {
                                pushPopButton.text = "Push"
                            }
                        }
                    }
                }.apply {
                    layoutParams = FrameLayout.LayoutParams(matchParent, matchParent)
                            .apply {
                                leftMargin = dip(16)
                                rightMargin = dip(16)
                                bottomMargin = dip(16)
                            }
                }
            }

        }

    }

    private fun doesListHaveItem(list: RecyclerView?) = getListItemCount(list) > 0

    private fun getListItemCount(list: RecyclerView?) = list?.adapter?.itemCount ?: 0
}

class Holder(val textView: TextView) : RecyclerView.ViewHolder(textView)

class Adapter(val arrayList: ArrayList<String> = ArrayList<String>()) : RecyclerView.Adapter<Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder? {
        return Holder(TextView(parent.context).apply {
            textSize = 20f
            background = context.obtainStyledAttributes(arrayOf(R.attr.selectableItemBackground).toIntArray()).getDrawable(0)
            verticalPadding = context.dip(8)
            isClickable = true
            layoutParams = ViewGroup.LayoutParams(matchParent, wrapContent)
        })
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.textView.text = arrayList.get(position)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    public fun push(text: String) {
        arrayList.add(0, text)
        notifyItemInserted(0)
    }

    public fun pop() {
        arrayList.remove(arrayList.last())
        notifyItemRemoved(arrayList.size)
    }

}
