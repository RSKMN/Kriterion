import React from 'react';
import { ReflectionResponse } from '@/services/financial-reflection.service';
import { History } from 'lucide-react';

interface ReflectionTimelineProps {
  reflections: ReflectionResponse[];
}

export const ReflectionTimeline: React.FC<ReflectionTimelineProps> = ({ reflections }) => {
  if (reflections.length === 0) return null;

  return (
    <div className="bg-white rounded-lg border border-gray-200 p-6">
      <h3 className="text-lg font-semibold text-gray-900 flex items-center gap-2 mb-6">
        <History className="w-5 h-5 text-slate-600" />
        Pattern Timeline
      </h3>
      
      <div className="relative border-l-2 border-slate-100 ml-3 space-y-8">
        {reflections.map((reflection, idx) => (
          <div key={idx} className="relative pl-8">
            <div className="absolute -left-[9px] top-1 w-4 h-4 rounded-full bg-white border-2 border-slate-300" />
            <div className="flex flex-col">
              <span className="text-xs font-medium text-slate-500 uppercase tracking-tight">
                {reflection.category} Analysis
              </span>
              <h4 className="text-sm font-semibold text-slate-900 mt-1">{reflection.title}</h4>
              <p className="text-sm text-slate-600 mt-1 leading-snug">
                {reflection.observation}
              </p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
