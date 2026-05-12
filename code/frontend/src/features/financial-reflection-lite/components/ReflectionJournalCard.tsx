import React from 'react';
import { ReflectionResponse } from '@/services/financial-reflection.service';
import { BookOpen, Sparkles, AlertTriangle, CheckCircle, Info } from 'lucide-react';

interface ReflectionJournalCardProps {
  reflections: ReflectionResponse[];
}

export const ReflectionJournalCard: React.FC<ReflectionJournalCardProps> = ({ reflections }) => {
  const getToneIcon = (tone: string) => {
    switch (tone) {
      case 'positive':
        return <CheckCircle className="w-4 h-4 text-emerald-500" />;
      case 'cautionary':
        return <AlertTriangle className="w-4 h-4 text-amber-500" />;
      case 'analytical':
        return <Sparkles className="w-4 h-4 text-blue-500" />;
      default:
        return <Info className="w-4 h-4 text-slate-400" />;
    }
  };

  const getToneStyles = (tone: string) => {
    switch (tone) {
      case 'positive':
        return 'bg-emerald-50 border-emerald-100 text-emerald-900';
      case 'cautionary':
        return 'bg-amber-50 border-amber-100 text-amber-900';
      case 'analytical':
        return 'bg-blue-50 border-blue-100 text-blue-900';
      default:
        return 'bg-slate-50 border-slate-100 text-slate-900';
    }
  };

  if (reflections.length === 0) {
    return (
      <div className="bg-white rounded-lg border border-gray-200 p-8 text-center">
        <BookOpen className="w-10 h-10 text-gray-300 mx-auto mb-3" />
        <h3 className="text-gray-900 font-medium">No reflections available</h3>
        <p className="text-sm text-gray-500 max-w-sm mx-auto mt-1">
          As more financial patterns emerge, reflective observations will be generated here.
        </p>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center gap-2 mb-2">
        <BookOpen className="w-5 h-5 text-gray-700" />
        <h2 className="text-lg font-semibold text-gray-900">Reflection Journal</h2>
      </div>
      
      {reflections.map((reflection, idx) => (
        <div 
          key={idx}
          className={`p-5 rounded-lg border ${getToneStyles(reflection.tone)} transition-all`}
        >
          <div className="flex items-start justify-between mb-2">
            <div className="flex items-center gap-2">
              {getToneIcon(reflection.tone)}
              <span className="text-xs font-bold uppercase tracking-wider opacity-70">
                {reflection.category}
              </span>
            </div>
            {reflection.isSignificant && (
              <span className="px-2 py-0.5 bg-white bg-opacity-50 rounded-full text-[10px] font-bold uppercase border border-black border-opacity-5">
                Significant Pattern
              </span>
            )}
          </div>
          <h4 className="font-semibold text-base mb-1">{reflection.title}</h4>
          <p className="text-sm leading-relaxed opacity-90">{reflection.observation}</p>
        </div>
      ))}
    </div>
  );
};
