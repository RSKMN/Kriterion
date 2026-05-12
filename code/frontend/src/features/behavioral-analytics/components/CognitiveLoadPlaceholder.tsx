import React from 'react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Progress } from '@/components/ui/progress';
import { Brain } from 'lucide-react';

export const CognitiveLoadPlaceholder: React.FC = () => {
    return (
        <Card className="border-dashed border-zinc-200 bg-white/40 dark:bg-zinc-900/30 backdrop-blur-sm h-full">
            <CardHeader className="pb-2">
                <CardTitle className="text-lg font-semibold flex items-center gap-2 text-zinc-700 dark:text-zinc-200">
                    <Brain className="w-5 h-5 text-zinc-400" />
                    Financial Cognitive Load
                </CardTitle>
                <CardDescription>
                    Not enough behavioral history yet to estimate cognitive load.
                </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
                <div className="space-y-2">
                    <div className="flex justify-between text-sm text-zinc-500">
                        <span>Aggregate Pressure</span>
                        <span>0/100</span>
                    </div>
                    <Progress value={0} className="h-2 bg-zinc-100 dark:bg-zinc-800" />
                </div>
                <p className="text-xs text-zinc-500 leading-relaxed">
                    Cognitive load estimates need repeated spending rhythms, recurring obligations, and category variation before the signal becomes meaningful.
                </p>
            </CardContent>
        </Card>
    );
};