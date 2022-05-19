package com.google.mlkit.vision.demo.kotlin.segmenter

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.demo.GraphicOverlay
import com.google.mlkit.vision.demo.kotlin.VisionProcessorBase
import com.google.mlkit.vision.demo.preference.PreferenceUtils
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.SegmentationMask
import com.google.mlkit.vision.segmentation.Segmenter
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions

/** A processor to run Segmenter.  */
class SegmenterProcessor :
    VisionProcessorBase<SegmentationMask> {
    private val segmenter: Segmenter

    constructor(context: Context) : this(context, /* isStreamMode= */ true)

    constructor(
        context: Context,
        isStreamMode: Boolean
    ) : super(
        context
    ) {
        val optionsBuilder = SelfieSegmenterOptions.Builder()
        optionsBuilder.setDetectorMode(
            if (isStreamMode) SelfieSegmenterOptions.STREAM_MODE
            else SelfieSegmenterOptions.SINGLE_IMAGE_MODE
        )
        if (PreferenceUtils.shouldSegmentationEnableRawSizeMask(context)) {
            optionsBuilder.enableRawSizeMask()
        }

        val options = optionsBuilder.build()
        segmenter = Segmentation.getClient(options)
        Log.d("TAG", "Segmented Processor created with option: $options")
    }

    override fun detectInImage(image: InputImage): Task<SegmentationMask> {
        return segmenter.process(image)
    }

    override fun onSuccess(
        results: SegmentationMask,
        graphicOverlay: GraphicOverlay
    ) {
        graphicOverlay.add(
            SegmentationGraphic(
                graphicOverlay,
                results
            )
        )
    }
}
