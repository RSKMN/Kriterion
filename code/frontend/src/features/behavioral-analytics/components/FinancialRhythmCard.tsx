import React from 'react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { ResponsiveContainer, AreaChart, Area, XAxis, YAxis, Tooltip, CartesianGrid } from 'recharts';
import { BehavioralMetrics } from '@/types/behavioral';
import { Clock, Calendar } from 'lucide-react';

interface FinancialRhythmCardProps {
    metrics: BehavioralMetrics;
}

export const FinancialRhythmCard: React.FC<FinancialRhythmCardProps> = ({ metrics }) => {
    const hourlyData = metrics.hourlyPatterns ? Object.entries(metrics.hourlyPatterns).map(([hour, value]) => ({
        hour: `${hour}:00`,
        density: value,
    })).sort((a, b) => parseInt(a.hour) - parseInt(b.hour)) : [];

    return (
        <Card className="border-none shadow-sm bg-white/50 dark:bg-zinc-900/50 backdrop-blur-sm">
            <CardHeader>
                <CardTitle className="text-lg font-semibold flex items-center gap-2">
                    <Clock className="w-5 h-5 text-blue-500" />
                    Financial Rhythm
                </CardTitle>
                <CardDescription>
                    Temporal distribution of financial activity and decision consistency.
                </CardDescription>
            </CardHeader>
            <CardContent>
                <div className="h-[200px] w-full">
                    <ResponsiveContainer width="100%" height="100%">
                        <AreaChart data={hourlyData}>
                            <defs>
                                <linearGradient id="colorDensity" x1="0" y1="0" x2="0" y2="1">
                                    <stop offset="5%" stopColor="#3b82f6" stopOpacity={0.3}/>
                                    <stop offset="95%" stopColor="#3b82f6" stopOpacity={0}/>
                                </linearGradient>
                            </defs>
                            <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#88888820" />
                            <XAxis 
                                dataKey="hour" 
                                fontSize={10} 
                                tickLine={false} 
                                axisLine={false}
                                interval={3}
                            />
                            <YAxis hide />
                            <Tooltip 
                                contentStyle={{ 
                                    backgroundColor: 'rgba(0,0,0,0.8)', 
                                    border: 'none', 
                                    borderRadius: '8px',
                                    fontSize: '12px',
                                    color: '#fff'
                                }}
                                itemStyle={{ color: '#fff' }}
                            />
                            <Area 
                                type="monotone" 
                                dataKey="density" 
                                stroke="#3b82f6" 
                                fillOpacity={1} 
                                fill="url(#colorDensity)" 
                                strokeWidth={2}
                            />
                        </AreaChart>
                    </ResponsiveContainer>
                </div>

                <div className="mt-6 grid grid-cols-2 gap-4">
                    <div className="space-y-1">
                        <span className="text-xs font-medium text-zinc-400 uppercase tracking-widest flex items-center gap-1">
                            <Clock className="w-3 h-3" /> Consistency
                        </span>
                        <div className="text-2xl font-bold text-zinc-800 dark:text-zinc-200">
                            {((metrics.transactionRhythmConsistency || 0) * 100).toFixed(1)}%
                        </div>
                    </div>
                    <div className="space-y-1">
                        <span className="text-xs font-medium text-zinc-400 uppercase tracking-widest flex items-center gap-1">
                            <Calendar className="w-3 h-3" /> Late Night Ratio
                        </span>
                        <div className="text-2xl font-bold text-zinc-800 dark:text-zinc-200">
                            {((metrics.lateNightSpendingRatio || 0) * 100).toFixed(1)}%
                        </div>
                    </div>
                </div>
            </CardContent>
        </Card>
    );
};
